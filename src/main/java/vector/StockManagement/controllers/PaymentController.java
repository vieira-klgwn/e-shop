package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.Payment;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.PaymentDTO;
import vector.StockManagement.model.enums.PaymentMethod;
import vector.StockManagement.repositories.InvoiceRepository;
import vector.StockManagement.services.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final InvoiceRepository invoiceRepository;

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
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentDTO paymentDTO,@AuthenticationPrincipal User user) {

        log.info("Received POST /api/payments/process - DTO: invoiceId={}, amount={}, method={}, txnRef={}",
                paymentDTO.getInvoiceId(), paymentDTO.getAmount(), paymentDTO.getMethod(), paymentDTO.getTxnRef());
        log.info("Authenticated user: id={}, email={}, roles={}", user.getId(), user.getEmail(), user.getRole());

        if (paymentDTO == null) {
            log.error("PaymentDTO is null - returning bad request");
            return ResponseEntity.badRequest().build();
        }

        try {
            Payment payment = paymentService.processPayment(
                    paymentDTO.getInvoiceId(),
                    paymentDTO.getAmount(),
                    paymentDTO.getMethod(),
                    paymentDTO.getTxnRef(),
                    user.getId()
            );
            log.info("Payment processed successfully - paymentId: {}", payment.getId());
            return ResponseEntity.ok(payment);
        } catch (RuntimeException re) {
            log.error("RuntimeException during payment processing for invoiceId: {}", paymentDTO.getInvoiceId(), re);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected exception during payment processing for invoiceId: {}", paymentDTO.getInvoiceId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Better to distinguish 400 vs 500
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
