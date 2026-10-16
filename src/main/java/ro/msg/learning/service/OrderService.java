package ro.msg.learning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.msg.learning.dto.CreateOrderRequest;
import ro.msg.learning.dto.OrderItemRequest;
import ro.msg.learning.entity.Address;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Order;
import ro.msg.learning.entity.OrderDetail;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.User;
import ro.msg.learning.repository.AddressRepository;
import ro.msg.learning.repository.OrderDetailRepository;
import ro.msg.learning.repository.OrderRepository;
import ro.msg.learning.repository.ProductRepository;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AddressRepository addressRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    private Location findNearestLocationWithStock(
            Map<UUID, Integer> productQuantities) {

        List<UUID> productIds = new ArrayList<>(
                productQuantities.keySet()
        );

        List<Stock> candidateStocks =
                stockRepository.findByProduct_IdInOrderByLocation_IdAsc(
                        productIds
                );

        Map<UUID, Map<UUID, Stock>> stockByLocation =
                new LinkedHashMap<>();

        for (Stock stock : candidateStocks) {
            UUID locationId = stock.getLocation().getId();
            UUID productId = stock.getProduct().getId();

            stockByLocation
                    .computeIfAbsent(locationId, id -> new HashMap<>())
                    .put(productId, stock);
        }

        for (Map.Entry<UUID, Map<UUID, Stock>> locationEntry
                : stockByLocation.entrySet()) {

            Map<UUID, Stock> locationStock = locationEntry.getValue();
            boolean canFulfillOrder = true;

            for (Map.Entry<UUID, Integer> item
                    : productQuantities.entrySet()) {

                Stock stock = locationStock.get(item.getKey());

                if (stock == null
                        || stock.getQuantity() < item.getValue()) {
                    canFulfillOrder = false;
                    break;
                }
            }

            if (canFulfillOrder) {
                return locationStock.values().iterator().next().getLocation();
            }
        }

        throw new IllegalStateException(
                "No location can fulfill the order with the given items."
        );
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        Map<UUID, Integer> productQuantities = new HashMap<>();

        for (OrderItemRequest item : request.getOrderItems()) {

            //adding the quantities of the same product if it appears multiple times in the order
            productQuantities.merge(
                    item.getProductId(),
                    item.getQuantity(),
                    Integer::sum
            );
        }

        //finding the user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + request.getUserId()
                ));

        //finding the delivery address
        Address address = addressRepository
                .findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Address not found with id: "
                                + request.getDeliveryAddressId()
                ));

        //finding a location with sufficient stock
        Location location =
                findNearestLocationWithStock(productQuantities);

        //creating the order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setCreatedAt(request.getTimeStamp());

        order = orderRepository.save(order);

        //creating order details and updating stock
        for (Map.Entry<UUID, Integer> entry
                : productQuantities.entrySet()) {

            UUID productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with id: " + productId
                    ));

            Stock stock = stockRepository
                    .findByProduct_IdAndLocation_Id(
                            productId,
                            location.getId()
                    )
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Stock not found for product id: " + productId
                    ));

            if (stock.getQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product id: " + productId
                );
            }

            //deducting the stock
            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);

            //creating the order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setShippedFromLocation(location);

            orderDetailRepository.save(orderDetail);
        }

        return order;
    }
}
