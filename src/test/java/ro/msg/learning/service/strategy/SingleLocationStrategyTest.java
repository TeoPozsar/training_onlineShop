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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SingleLocationStrategyTest {

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

        laptop = createProduct("laptop");
        phone = createProduct("phone");
        location1 = createLocation();
        location2 = createLocation();
    }

    @Test
    void allocate_WhenOneLocationHasAllProducts_AllocatesEntireOrderThere() {
        Stock laptopStock = createStock(laptop, location1, 10);
        Stock phoneStock = createStock(phone, location1, 15);

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
        Stock laptopStock = createStock(laptop, location1, 10);
        Stock phoneStock = createStock(phone, location2, 15);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(laptopStock, phoneStock));

        assertThrows(InsufficientStockException.class, () ->
                singleLocationStrategy.allocate(Map.of(
                        laptop.getId(), 5,
                        phone.getId(), 10
                )));
    }

    @Test
    void allocate_WhenOneProductHasInsufficientStock_ThrowsException() {
        Stock laptopStock = createStock(laptop, location1, 4);
        Stock phoneStock = createStock(phone, location1, 15);

        when(stockRepository.findByProduct_IdInOrderByLocation_IdAsc(anyList()))
                .thenReturn(List.of(laptopStock, phoneStock));

        assertThrows(InsufficientStockException.class, () ->
                singleLocationStrategy.allocate(Map.of(
                        laptop.getId(), 5,
                        phone.getId(), 10
                )));
    }

    private Product createProduct(String name) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        return product;
    }

    private Location createLocation() {
        Location location = new Location();
        location.setId(UUID.randomUUID());
        return location;
    }

    private Stock createStock(Product product, Location location, int quantity) {
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        stock.setQuantity(quantity);
        return stock;
    }
}
