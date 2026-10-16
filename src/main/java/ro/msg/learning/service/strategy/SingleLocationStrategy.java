
package ro.msg.learning.service.strategy;

import lombok.RequiredArgsConstructor;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.exception.InsufficientStockException;
import ro.msg.learning.repository.StockRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class SingleLocationStrategy implements OrderStrategy {

    private final StockRepository stockRepository;

    @Override
    public List<StockAllocation> allocate(
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

        Map<UUID, Stock> selectedLocationStock =
                stockByLocation.values()
                        .stream()
                        .filter(locationStock ->
                                productQuantities.entrySet()
                                        .stream()
                                        .allMatch(item -> {
                                            Stock stock = locationStock.get(
                                                    item.getKey()
                                            );

                                            return stock != null
                                                    && stock.getQuantity()
                                                    >= item.getValue();
                                        })
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new InsufficientStockException(
                                        "No location can fulfill the order "
                                                + "with the given items."
                                )
                        );

        Location selectedLocation =
                selectedLocationStock.values()
                        .iterator()
                        .next()
                        .getLocation();

        return productQuantities.entrySet()
                .stream()
                .map(item -> {
                    Stock stock = selectedLocationStock.get(
                            item.getKey()
                    );

                    return new StockAllocation(
                            selectedLocation,
                            stock.getProduct(),
                            item.getValue()
                    );
                })
                .toList();
    }
}
