package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.StockId;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, StockId> {

    Optional<Stock> findByProductId_AndLocationId(UUID productId, UUID locationId);
}