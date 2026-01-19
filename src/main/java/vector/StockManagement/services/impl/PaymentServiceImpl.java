package vector.StockManagement.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.Payment;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.PaymentDTO;
import vector.StockManagement.model.enums.InvoiceStatus;
import vector.StockManagement.model.enums.PaymentMethod;
import vector.StockManagement.model.enums.PaymentStatus;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.PaymentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TenantRepository tenantRepository;
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public List<PaymentDTO> findAll(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("User not found"));
        List<PaymentDTO> paymentDTOs = new ArrayList<>();
        for (Payment payment: paymentRepository.findAllByPostedBy(user)){
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setOrderBy(payment.getInvoice().getOrder().getCreatedBy());
            paymentDTO.setCreatedBy(payment.getPostedBy());
            paymentDTO.setTxnRef(payment.getTxnRef());
            paymentDTO.setMethod(payment.getMethod());
            paymentDTO.setAmount(payment.getAmount());
            payment.setInvoice(payment.getInvoice());
            paymentDTOs.add(paymentDTO);
        }

        return paymentDTOs;
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
    public Payment processPayment(Long invoiceId, Long amount, PaymentMethod method, String txnRef, Long userId) {


        // ... other fields like repositories


            log.info("Starting payment processing - invoiceId: {}, amount: {}, method: {}, txnRef: {}, userId: {}",
                    invoiceId, amount, method, txnRef, userId);

            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
            log.info("Found invoice: {} with issuedTo: {}", invoice.getId(), invoice.getIssuedTo() != null ? invoice.getIssuedTo().getId() : "null");

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            log.info("Found user (postedBy): {} - email: {}", user.getId(), user.getEmail());

            // Create payment record
            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setTxnRef(txnRef);
            payment.setPaidAt(LocalDateTime.now());
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPostedBy(user);

            // Note: Adding explicit set for missing fields based on entity requirements
            payment.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "USD");  // Pull from invoice or default
            payment.setTenant(invoice.getTenant());

            log.info("Created payment object - amount: {}, currency: '{}', method: {}, txnRef: {}, status: {}, tenant: {}",
                    payment.getAmount(), payment.getCurrency(), payment.getMethod(), payment.getTxnRef(),
                    payment.getPaymentStatus(), payment.getTenant() != null ? payment.getTenant().getId() : "null");

            try {
                Payment savedPayment = paymentRepository.saveAndFlush(payment);
                log.info("Payment saved successfully: {}", savedPayment.getId());
            } catch (Exception e) {
                log.error("Error during payment saveAndFlush for invoiceId: {}", invoiceId, e);
                throw e;  // Re-throw to propagate
            }

            // Update invoice with payment and remove deduct it from the user's invoice
//        invoice.addPayment(amount);
        invoice.setStatus(InvoiceStatus.PAID);

            try {
                User invoiceOwner = invoice.getIssuedTo();
                log.info("Invoice owner: {} - email: {}",
                        invoiceOwner != null ? invoiceOwner.getId() : "null",
                        invoiceOwner != null ? invoiceOwner.getEmail() : "null");

                if (invoiceOwner != null) {
                    log.info("Owner's invoices size before remove: {}", invoiceOwner.getInvoices().size());
                    boolean removed = invoiceOwner.getInvoices().remove(invoice);
                    log.info("Invoice removed from owner: {}", removed);
                    userRepository.saveAndFlush(invoiceOwner);
                    log.info("Owner saved after invoice removal");
                } else {
                    log.warn("No invoice owner found - skipping removal");
                }

                invoiceRepository.saveAndFlush(invoice);
                invoiceOwner.setCredit(invoiceOwner.getCredit() - amount);
                userRepository.saveAndFlush(invoiceOwner);
                log.info("Invoice saved after processing");
            } catch (Exception e) {
                log.error("Error during post-payment updates for invoiceId: {}", invoiceId, e);
                throw e;  // Re-throw to propagate
            }

            log.info("Payment processing completed successfully for invoiceId: {}", invoiceId);

            return paymentRepository.findById(payment.getId()).orElse(null);  // Re-fetch if needed, or return the saved one
        }



}
