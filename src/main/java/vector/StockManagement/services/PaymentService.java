package vector.StockManagement.services;

import vector.StockManagement.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();
    Payment findById(Long id);
    Payment save(Payment payment);
    void delete(Long id);
}