package vector.StockManagement.services;

import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.InvoiceDisplayDTO;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDisplayDTO> findAll(User user);

    List<InvoiceDisplayDTO> getAll();

    InvoiceDisplayDTO findById(Long id);
    Invoice getInvoice(Long id);
    Invoice save(Invoice invoice);
    Invoice update(Long invoiceId);
    void delete(Long id);
}