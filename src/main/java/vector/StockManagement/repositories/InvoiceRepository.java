package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
