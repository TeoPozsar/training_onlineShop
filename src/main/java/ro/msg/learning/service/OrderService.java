package ro.msg.learning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.dto.OrderItemRequest;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.repository.AddressRepository;
import ro.msg.learning.repository.LocationRepository;
import ro.msg.learning.repository.OrderDetailRepository;
import ro.msg.learning.repository.OrderRepository;
import ro.msg.learning.repository.ProductRepository;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.repository.UserRepository;
import ro.msg.learning.dto.CreateOrderRequest;
import ro.msg.learning.entity.Address;
import ro.msg.learning.entity.Order;
import ro.msg.learning.entity.OrderDetail;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AddressRepository addressRepository;
    private final LocationRepository locationRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    private Location findNearestLocationWithStock(List<OrderItemRequest> items) {

        Map<UUID, Integer> productQuantities = new HashMap<>();
        for (OrderItemRequest item : items) {
            productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        List<Location> locations = locationRepository.findAllByOrderByIdAsc();

        for (Location location : locations) {
            boolean canFulfillOrder = true;

            for (Map.Entry<UUID, Integer> item : productQuantities.entrySet()) {
                Stock stock = stockRepository.
                        findByProductId_AndLocationId(item.getKey(), location.getId())
                        .orElse(null);

                if (stock == null || stock.getQuantity() < item.getValue()) {
                    canFulfillOrder = false;
                    break;
                }
            }

            if (canFulfillOrder) {
                return location;
            }
        }

        throw new IllegalStateException("No location can fulfill the order with the given items.");
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        //finding the user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        //finding the address
        Address address = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + request.getDeliveryAddressId()));

        //finding the nearest location with stock
        Location location = findNearestLocationWithStock(request.getOrderItems());

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setCreatedAt(request.getTimeStamp());
        order = orderRepository.save(order);

        Map<UUID, Integer> productQuantities = new HashMap<>();

        for (OrderItemRequest item : request.getOrderItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null.");
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Product quantity must be greater than zero.");
            }
            productQuantities.merge
                    (item.getProductId(),
                            item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            UUID productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

            Stock stock = stockRepository.findByProductId_AndLocationId(productId, location.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Stock not found for product id: " + productId));

            if (stock.getQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient stock for product id: " + productId);
            }

            // Deduct the stock quantity
            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);

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
