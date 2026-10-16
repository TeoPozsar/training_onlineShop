package ro.msg.learning.service.strategy;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.exception.InsufficientStockException;
import ro.msg.learning.repository.StockRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MostAbundantStrategy implements OrderStrategy {

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

        Map<UUID, List<Stock>> stockByProduct = new HashMap<>();

        for (Stock stock : candidateStocks) {
            UUID productId = stock.getProduct().getId();
            stockByProduct
                    .computeIfAbsent(productId, id -> new ArrayList<>())
                    .add(stock);
        }

        List<StockAllocation> allocations = new ArrayList<>();

        for (Map.Entry<UUID, Integer> item : productQuantities.entrySet()) {
            UUID productId = item.getKey();
            int requestedQuantity = item.getValue();

            Stock bestStock = null;

            for (Stock stock : stockByProduct.getOrDefault(productId, Collections.emptyList())) {
                if (stock.getQuantity() >= requestedQuantity && (bestStock == null || stock.getQuantity() > bestStock.getQuantity())) {
                    bestStock = stock;
                }

            }

            if (bestStock == null) {
                throw new InsufficientStockException(
                        "No location has enough stock for product: " + productId
                );
            }

            allocations.add(new StockAllocation(
                    bestStock.getLocation(),
                    bestStock.getProduct(),
                    requestedQuantity
            ));

        }

        return allocations;
    }
}
