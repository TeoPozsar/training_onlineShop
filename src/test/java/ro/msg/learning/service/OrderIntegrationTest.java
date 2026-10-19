package ro.msg.learning.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import ro.msg.learning.dto.CreateOrderRequest;
import ro.msg.learning.dto.OrderItemRequest;
import ro.msg.learning.entity.Address;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Order;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.User;
import ro.msg.learning.enums.Role;
import ro.msg.learning.exception.InsufficientStockException;
import ro.msg.learning.repository.AddressRepository;
import ro.msg.learning.repository.OrderDetailRepository;
import ro.msg.learning.repository.OrderRepository;
import ro.msg.learning.repository.ProductCategoryRepository;
import ro.msg.learning.repository.ProductRepository;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.repository.UserRepository;
import ro.msg.learning.service.strategy.OrderStrategy;
import ro.msg.learning.service.strategy.SingleLocationStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class OrderIntegrationTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:17");


    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @TestConfiguration
    static class StrategyTestConfig {
        @Bean
        @Primary
        OrderStrategy testOrderStrategy(StockRepository stockRepository) {
            return new SingleLocationStrategy(stockRepository);
        }
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private User user;
    private Address deliveryAddress;
    private Product product;
    private Location location;
    private Stock stock;

    @BeforeEach
    void setUp() {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        locationRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Create the user
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPasswordHash("test-password");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        // Create the delivery address
        deliveryAddress = new Address();
        deliveryAddress.setCountry("Romania");
        deliveryAddress.setCity("Timisoara");
        deliveryAddress.setCounty("Timis");
        deliveryAddress.setStreet("Test Street 1");
        deliveryAddress = addressRepository.save(deliveryAddress);

        // Create a location
        location = new Location();
        location.setName("Test Warehouse");
        location.setAddress(deliveryAddress);
        location = saveLocation(location);

        // Create a product category
        ProductCategory category = new ProductCategory();
        category.setName("Electronics");
        category.setDescription("Electronic products");
        category = productCategoryRepository.save(category);

        // Create a product
        product = new Product();
        product.setName("Laptop");
        product.setDescription("Test laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setWeight(2.5);
        product.setCategory(category);
        product.setImageURL("laptop.jpg");
        product = productRepository.save(product);

        // Create stock for the product at the location
        stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        stock.setQuantity(10);
        stock = stockRepository.save(stock);
    }

    private Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Autowired
    private ro.msg.learning.repository.LocationRepository locationRepository;

    private CreateOrderRequest createRequest(int quantity) {
        OrderItemRequest item = OrderItemRequest.builder()
                .productId(product.getId())
                .quantity(quantity)
                .build();

        return CreateOrderRequest.builder()
                .userId(user.getId())
                .deliveryAddressId(deliveryAddress.getId())
                .timeStamp(LocalDateTime.now())
                .orderItems(List.of(item))
                .build();
    }

    @Test
    void createOrder_WhenStockIsSufficient_DeductsStock() {
        Order createdOrder = orderService.createOrder(createRequest(5));

        assertNotNull(createdOrder.getId());
        assertEquals(user.getId(), createdOrder.getUser().getId());
        assertEquals(deliveryAddress.getId(), createdOrder.getAddress().getId());

        Stock updatedStock = stockRepository.findByProduct_IdAndLocation_Id(product.getId(), location.getId()).orElseThrow();
        assertEquals(5, updatedStock.getQuantity());
    }

    @Test
    void createOrder_WhenStockIsInsufficient_ThrowsExceptionAndRollsBack() {
        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(createRequest(20))
        );

        assertEquals(0, orderRepository.count());
        assertEquals(0, orderDetailRepository.count());

        Stock unchangedStock = stockRepository
                .findByProduct_IdAndLocation_Id(
                        product.getId(),
                        location.getId()
                )
                .orElseThrow();

        assertEquals(10, unchangedStock.getQuantity());
    }

}
