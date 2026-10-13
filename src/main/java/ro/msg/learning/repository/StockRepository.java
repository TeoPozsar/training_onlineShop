package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.Stock;
import ro.msg.learning.entity.StockId;

public interface StockRepository extends JpaRepository<Stock, StockId> {
}