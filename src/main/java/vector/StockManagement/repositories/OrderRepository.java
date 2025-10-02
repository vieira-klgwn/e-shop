package vector.StockManagement.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order getOrderById(Long id);

    List<Order> findByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.orderAmount),0) from Order o where (:from is null or o.createdAt >= :from) and (:to is null or o.createdAt <= :to)")
    Long sumOrderAmountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select count(o) from Order o where (:from is null or o.createdAt >= :from) and (:to is null or o.createdAt <= :to)")
    Long countOrdersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.createdBy WHERE o.id = :id")
    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderLines ol JOIN FETCH ol.tenant WHERE o.tenant.id = :tenantId")
    Page<Order> findAllByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    List<Order> findAllByCreatedBy(User createdBy);

    List<Order> findAllByStatus(@NotNull OrderStatus status);
}
