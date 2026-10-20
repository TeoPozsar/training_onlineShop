package ro.msg.learning.util;

import ro.msg.learning.entity.*;
import ro.msg.learning.enums.Role;

import java.util.UUID;
import java.math.BigDecimal;

public final class TestData {

    private TestData() {
        //to prevent instantiation
    }

    public static Product createProduct(String name, String description, BigDecimal price, ProductCategory category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        return product;
    }

    public static Location createLocation(String name, Address address) {
        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        return location;
    }

    public static Stock createStock(Product product, Location location, int quantity) {
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setLocation(location);
        stock.setQuantity(quantity);
        return stock;
    }

    public static User createUser(String username , String email , String firstName , String lastName , String passwordHash, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        return user;
    }

    public static Address createDeliveryAddress(String country, String city, String county, String street) {
        Address deliveryAddress = new Address();
        deliveryAddress.setCountry(country);
        deliveryAddress.setCity(city);
        deliveryAddress.setCounty(county);
        deliveryAddress.setStreet(street);
        return deliveryAddress;
    }

    public static ProductCategory createProductCategory(String name, String description) {
        ProductCategory category = new ProductCategory();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    public static Product createProductWithId(
            String name,
            String description,
            BigDecimal price,
            ProductCategory category) {

        Product product = createProduct(name, description, price, category);
        product.setId(UUID.randomUUID());
        return product;
    }

    public static Location createLocationWithId(String name, Address address) {
        Location location = createLocation(name, address);
        location.setId(UUID.randomUUID());
        return location;
    }

    public static ProductCategory createProductCategoryWithId(
            String name, String description) {

        ProductCategory category = createProductCategory(name, description);
        category.setId(UUID.randomUUID());
        return category;
    }


}
