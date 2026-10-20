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

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import tools.jackson.databind.ObjectMapper;

import ro.msg.learning.dto.CreateOrderRequest;
import ro.msg.learning.dto.OrderItemRequest;
import ro.msg.learning.entity.Address;
import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.User;
import ro.msg.learning.enums.Role;
import ro.msg.learning.repository.AddressRepository;
import ro.msg.learning.repository.LocationRepository;
import ro.msg.learning.repository.OrderDetailRepository;
import ro.msg.learning.repository.OrderRepository;
import ro.msg.learning.repository.ProductCategoryRepository;
import ro.msg.learning.repository.ProductRepository;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.repository.UserRepository;
import ro.msg.learning.service.strategy.OrderStrategy;
import ro.msg.learning.service.strategy.SingleLocationStrategy;
import ro.msg.learning.util.TestData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private AddressRepository addressRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private User user;
    private Address deliveryAddress;
    private Product product;
    private Location location;
    private Stock stock;

    @BeforeEach
    void setUp() {
        databaseCleanup();

        // Create the user
        user = TestData.createUser("testuser", "testuser@example.com", "Test", "User", "test-password", Role.USER);
        user = userRepository.save(user);

        // Create the delivery address
        deliveryAddress = TestData.createDeliveryAddress("USA", "New York", "New York County", "123 Main St");
        deliveryAddress = addressRepository.save(deliveryAddress);

        // Create a location
        location = TestData.createLocation("Warehouse 1", deliveryAddress);
        location = locationRepository.save(location);

        // Create a product category
        ProductCategory category = TestData.createProductCategory("Electronics", "Electronic products");
        category = productCategoryRepository.save(category);

        // Create a product
        product = TestData.createProduct("Laptop", "A high-performance laptop", new BigDecimal("1500.00"), category);
        product = productRepository.save(product);

        // Create stock for the product at the location
        stock = TestData.createStock(product, location, 10);
        stock = stockRepository.save(stock);

    }

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
    void createOrder_WhenStockIsSufficient_DeductsStock() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(5))))
                .andExpect(status().isCreated());

        Stock updatedStock = stockRepository
                .findByProduct_IdAndLocation_Id(
                        product.getId(), location.getId())
                .orElseThrow();

        assertEquals(5, updatedStock.getQuantity());
    }

    @Test
    void createOrder_WhenStockIsInsufficient_ReturnsErrorAndRollsBack() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(20))))
                .andExpect(status().isConflict());

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

    private void databaseCleanup() {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        locationRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

}
