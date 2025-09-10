package vector.StockManagement.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.Payment;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.PaymentMethod;
import vector.StockManagement.model.enums.PaymentStatus;
import vector.StockManagement.repositories.InvoiceRepository;
import vector.StockManagement.repositories.PaymentRepository;
import vector.StockManagement.services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
    
    @Transactional
    @Override
    public Payment processPayment(Long invoiceId, BigDecimal amount, PaymentMethod method, String txnRef) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = null;
        if (principal instanceof User) {
            user = (User) principal;
        }
        
        if (user == null) {
            throw new RuntimeException("User must be authenticated to process payments");
        }
        
        // Create payment record
        Payment payment = new Payment(invoice, method, amount, invoice.getCurrency(), user);
        payment.setTxnRef(txnRef);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice with payment
        invoice.addPayment(amount);
        invoiceRepository.save(invoice);
        
        return savedPayment;
    }
}
