package ro.msg.learning.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.LinkedHashMap;



@Component
@RequiredArgsConstructor
public class SingleLocationStrategy implements OrderStrategy {
    private final StockRepository stockRepository;

    @Override
    public List<StockAllocation> allocate(Map<UUID, Integer> productQuantities) {

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

                Location location = locationStock.values().iterator().next().getLocation();

                List<StockAllocation> allocations = new ArrayList<>();
                for (Map.Entry<UUID, Integer> item
                        : productQuantities.entrySet()) {
                    Stock stock = locationStock.get(item.getKey());
                    allocations.add(new StockAllocation(
                            stock.getLocation(),
                            stock.getProduct(),
                            item.getValue()
                    ));
                }
                return allocations;

            }
        }

            throw new IllegalStateException(
                    "No location can fulfill the order with the given items."
            );


    }
}
