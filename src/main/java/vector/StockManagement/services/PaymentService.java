package vector.StockManagement.services;

import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.Payment;
import vector.StockManagement.model.dto.PaymentDTO;
import vector.StockManagement.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {


    List<PaymentDTO> findAll(Long userId);

    Payment findById(Long id);
    Payment save(Payment payment);
    void delete(Long id);
    Payment processPayment(Long invoiceId, Long amount, PaymentMethod method, String txnRef, Long userId);
}
