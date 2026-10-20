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
import ro.msg.learning.util.TestData;

import java.math.BigDecimal;
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

        product = TestData.createProductWithId(
                "laptop",
                "A high-performance laptop",
                new BigDecimal("1500.00"),
                null
        );
        productId = product.getId();

        location1 = TestData.createLocationWithId("Warehouse 1", null);
        location2 = TestData.createLocationWithId("Warehouse 2", null);
    }

    @Test
    void allocate_WhenMultipleStocksAvailable_SelectsLocationWithHighestQuantity() {
        Stock stock1 = TestData.createStock(product, location1, 10);
        Stock stock2 = TestData.createStock(product, location2, 20);
        Stock stock3 = TestData.createStock(product, location1, 5);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
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
        Stock stock1 = TestData.createStock(product, location1, 5);
        Stock stock2 = TestData.createStock(product, location2, 3);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(stock1, stock2));

        assertThrows(
                InsufficientStockException.class,
                () -> mostAbundantStrategy.allocate(Map.of(productId, 10))
        );
    }

    @Test
    void allocate_WhenProductsComeFromDifferentLocations_AllocatesEachProduct() {
        Product product2 = TestData.createProductWithId(
                "phone",
                "A smartphone",
                new BigDecimal("800.00"),
                null
        );
        UUID product2Id = product2.getId();

        Stock laptopStock1 = TestData.createStock(product, location1, 10);
        Stock laptopStock2 = TestData.createStock(product, location2, 15);
        Stock phoneStock1 = TestData.createStock(product2, location1, 5);
        Stock phoneStock2 = TestData.createStock(product2, location2, 20);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
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
        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of());

        assertThrows(
                InsufficientStockException.class,
                () -> mostAbundantStrategy.allocate(Map.of(productId, 1))
        );
    }
}
