package vector.StockManagement.services;

import vector.StockManagement.model.Invoice;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAll();
    Invoice findById(Long id);
    Invoice save(Invoice invoice);
    void delete(Long id);
}