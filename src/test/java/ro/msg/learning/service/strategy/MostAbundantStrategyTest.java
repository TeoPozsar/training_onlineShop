
package ro.msg.learning.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.exception.InsufficientStockException;
import ro.msg.learning.repository.StockRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MostAbundantStrategyTest {

    @Mock
    private StockRepository stockRepository;

    private MostAbundantStrategy mostAbundantStrategy;

    private Product product;
    private Location location1;
    private Location location2;
    private UUID productId;

    @BeforeEach
    void setUp() {
        mostAbundantStrategy = new MostAbundantStrategy(stockRepository);

        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("laptop");

        location1 = new Location();
        location1.setId(UUID.randomUUID());

        location2 = new Location();
        location2.setId(UUID.randomUUID());
    }

    private Stock createStock(
            Product product,
            Location location,
            int quantity
    ) {
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        stock.setQuantity(quantity);
        return stock;
    }

    @Test
    void allocate_WhenMultipleStocksAvailable_SelectsLocationWithHighestQuantity() {
        Stock stock1 = createStock(product, location1, 10);
        Stock stock2 = createStock(product, location2, 20);
        Stock stock3 = createStock(product, location1, 5);

        when(stockRepository
                .findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(stock1, stock2, stock3));

        List<StockAllocation> allocation =
                mostAbundantStrategy.allocate(Map.of(productId, 7));

        assertEquals(1, allocation.size());
        assertSame(location2, allocation.get(0).location());
        assertSame(product, allocation.get(0).product());
        assertEquals(7, allocation.get(0).quantity());

        verify(stockRepository)
                .findByProduct_IdInOrderByLocation_IdAsc(anyList());
    }

    @Test
    void allocate_WhenInsufficientStock_ThrowsException() {
        Stock stock1 = createStock(product, location1, 5);
        Stock stock2 = createStock(product, location2, 3);

        when(stockRepository
                .findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(stock1, stock2));

        assertThrows(
                InsufficientStockException.class,
                () -> mostAbundantStrategy.allocate(Map.of(productId, 10))
        );
    }

    @Test
    void allocate_WhenProductsComeFromDifferentLocations_AllocatesEachProduct() {
        Product product2 = new Product();
        UUID product2Id = UUID.randomUUID();
        product2.setId(product2Id);
        product2.setName("phone");

        Stock laptopStock1 = createStock(product, location1, 10);
        Stock laptopStock2 = createStock(product, location2, 15);
        Stock phoneStock1 = createStock(product2, location1, 5);
        Stock phoneStock2 = createStock(product2, location2, 20);

        when(stockRepository
                .findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(
                        laptopStock1,
                        laptopStock2,
                        phoneStock1,
                        phoneStock2
                ));

        List<StockAllocation> allocation = mostAbundantStrategy.allocate(
                Map.of(productId, 5, product2Id, 10)
        );

        assertEquals(2, allocation.size());

        StockAllocation laptopAllocation = allocation.stream()
                .filter(a -> a.product().getId().equals(productId))
                .findFirst()
                .orElseThrow();

        StockAllocation phoneAllocation = allocation.stream()
                .filter(a -> a.product().getId().equals(product2Id))
                .findFirst()
                .orElseThrow();

        assertSame(location2, laptopAllocation.location());
        assertEquals(5, laptopAllocation.quantity());

        assertSame(location2, phoneAllocation.location());
        assertEquals(10, phoneAllocation.quantity());
    }

    @Test
    void allocate_WhenProductHasNoStock_ThrowsException() {
        when(stockRepository
                .findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of());

        assertThrows(
                InsufficientStockException.class,
                () -> mostAbundantStrategy.allocate(Map.of(productId, 1))
        );
    }
}
