package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.OrderDetail;
import ro.msg.learning.entity.OrderDetailId;

import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
