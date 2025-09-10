package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    @Query("select coalesce(sum(p.amount),0) from Payment p where (:from is null or p.paidAt >= :from) and (:to is null or p.paidAt <= :to)")
    Long sumPaymentAmountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
