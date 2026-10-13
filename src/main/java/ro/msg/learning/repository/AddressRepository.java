package ro.msg.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.msg.learning.entity.Address;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
