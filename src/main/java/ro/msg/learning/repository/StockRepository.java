package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.StockId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, StockId> {

    Optional<Stock> findByProduct_IdAndLocation_Id(UUID productId, UUID locationId);

    List<Stock> findByProduct_IdInOrderByLocation_IdAsc(List<UUID> productIds);

    Optional<Stock> findByProduct_IdAndLocation_IdAndQuantityGreaterThanEqual(UUID productId, UUID locationId, int quantity);


}