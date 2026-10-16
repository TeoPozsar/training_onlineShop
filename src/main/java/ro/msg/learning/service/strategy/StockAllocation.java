package ro.msg.learning.service.strategy;

import ro.msg.learning.entity.Location;
import ro.msg.learning.entity.Product;

public record StockAllocation(
        Location location,
        Product product,
        int quantity
) {
}
