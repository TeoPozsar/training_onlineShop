package ro.msg.learning.service.strategy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderStrategy {

    List<StockAllocation> allocate(Map<UUID, Integer> productQuantities);
}
