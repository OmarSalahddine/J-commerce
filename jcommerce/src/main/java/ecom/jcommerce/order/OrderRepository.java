package ecom.jcommerce.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Loads the order and its lines in one query instead of 1 + N.
    @EntityGraph(attributePaths = "lines")
    Optional<Order> findWithLinesById(Long id);
}