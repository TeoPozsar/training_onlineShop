package ro.msg.learning.service.strategy;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.exception.InsufficientStockException;
import ro.msg.learning.repository.StockRepository;

import java.util.*;


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

        return productQuantities.entrySet()
                .stream()
                .map(item -> {
                    UUID productId = item.getKey();
                    int requestedQuantity = item.getValue();

                    Stock bestStock = stockByProduct.getOrDefault(productId, Collections.emptyList())
                            .stream()
                            .filter(stock -> stock.getQuantity() >= requestedQuantity)
                            .max(Comparator.comparingInt(Stock::getQuantity))
                            .orElseThrow(() -> new InsufficientStockException(
                                    "Insufficient stock for product with id: " + productId
                            ));

                    return new StockAllocation(
                            bestStock.getLocation(),
                            bestStock.getProduct(),
                            requestedQuantity
                    );
                })
                .toList();
    }
}




