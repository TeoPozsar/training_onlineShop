package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.Product;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
