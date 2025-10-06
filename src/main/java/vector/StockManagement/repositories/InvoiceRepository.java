package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.User;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByOrder_CreatedBy(User orderCreatedBy);


}
