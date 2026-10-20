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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleLocationStrategyTest {

    @Mock
    private StockRepository stockRepository;

    private SingleLocationStrategy singleLocationStrategy;
    private Product laptop;
    private Product phone;
    private Location location1;
    private Location location2;

    @BeforeEach
    void setUp() {
        singleLocationStrategy = new SingleLocationStrategy(stockRepository);

        laptop = TestData.createProductWithId(
                "laptop",
                "A high-performance laptop",
                new BigDecimal("1500.00"),
                null
        );

        phone = TestData.createProductWithId(
                "phone",
                "A smartphone",
                new BigDecimal("800.00"),
                null
        );

        location1 = TestData.createLocationWithId("Warehouse 1", null);
        location2 = TestData.createLocationWithId("Warehouse 2", null);
    }

    @Test
    void allocate_WhenOneLocationHasAllProducts_AllocatesEntireOrderThere() {
        Stock laptopStock = TestData.createStock(laptop, location1, 10);
        Stock phoneStock = TestData.createStock(phone, location1, 15);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(laptopStock, phoneStock));

        List<StockAllocation> allocation = singleLocationStrategy.allocate(Map.of(
                laptop.getId(), 5,
                phone.getId(), 10
        ));

        assertEquals(2, allocation.size());

        StockAllocation laptopAllocation = allocation.stream()
                .filter(item -> item.product() == laptop)
                .findFirst()
                .orElseThrow();

        StockAllocation phoneAllocation = allocation.stream()
                .filter(item -> item.product() == phone)
                .findFirst()
                .orElseThrow();

        assertSame(location1, laptopAllocation.location());
        assertEquals(5, laptopAllocation.quantity());

        assertSame(location1, phoneAllocation.location());
        assertEquals(10, phoneAllocation.quantity());
    }

    @Test
    void allocate_WhenProductsAreAvailableOnlyAtDifferentLocations_ThrowsException() {
        Stock laptopStock = TestData.createStock(laptop, location1, 10);
        Stock phoneStock = TestData.createStock(phone, location2, 15);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(laptopStock, phoneStock));

        assertThrows(InsufficientStockException.class, () ->
                singleLocationStrategy.allocate(Map.of(
                        laptop.getId(), 5,
                        phone.getId(), 10
                ))
        );
    }

    @Test
    void allocate_WhenOneProductHasInsufficientStock_ThrowsException() {
        Stock laptopStock = TestData.createStock(laptop, location1, 4);
        Stock phoneStock = TestData.createStock(phone, location1, 15);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(laptopStock, phoneStock));

        assertThrows(InsufficientStockException.class, () ->
                singleLocationStrategy.allocate(Map.of(
                        laptop.getId(), 5,
                        phone.getId(), 10
                ))
        );
    }
}
