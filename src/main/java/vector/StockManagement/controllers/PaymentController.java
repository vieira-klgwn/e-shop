package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Payment;
import vector.StockManagement.model.enums.PaymentMethod;
import vector.StockManagement.services.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<Payment> getAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        Payment payment = paymentService.findById(id);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payment> processPayment(
            @RequestParam Long invoiceId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String txnRef) {
        
        try {
            Payment payment = paymentService.processPayment(invoiceId, amount, method, txnRef);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public Payment create(@RequestBody Payment payment) {
        return paymentService.save(payment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Payment> update(@PathVariable Long id, @RequestBody Payment payment) {
        Payment existing = paymentService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        payment.setPaymentStatus(existing.getPaymentStatus());
        return ResponseEntity.ok(paymentService.save(payment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
