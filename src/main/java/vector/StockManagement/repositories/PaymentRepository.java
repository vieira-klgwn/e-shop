package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
