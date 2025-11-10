package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.config.TenantAwareValidator;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.model.TenantScoped;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.InvoiceDisplayDTO;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final TenantAwareValidator tenantValidator;
    private final UserRepository userRepository;

    @GetMapping("/overdue")
//    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SALES_MANAGER','WAREHOUSE_MANAGER','STORE_MANAGER')")
    public Page<InvoiceDisplayDTO> getAllOverdueInvoices(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @PathVariable Long id) {
        Pageable pageable = PageRequest.of(page, size);
        User user = userRepository.findById(id).orElseThrow(() -> new  IllegalStateException("User not found"));
        List<InvoiceDisplayDTO> invoices = invoiceService.findOverdueInvoices();
        int start = Math.min((int) pageable.getOffset(), invoices.size());
        int end = Math.min(start + pageable.getPageSize(), invoices.size());
        return new PageImpl<>(invoices.subList(start, end), pageable, invoices.size());
    }

    @GetMapping("/allInvoices/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SALES_MANAGER','WAREHOUSE_MANAGER','STORE_MANAGER')")
    public Page<InvoiceDisplayDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @PathVariable Long id) {
        Pageable pageable = PageRequest.of(page, size);
        User user = userRepository.findById(id).orElseThrow(() -> new  IllegalStateException("User not found"));
        List<InvoiceDisplayDTO> invoices = invoiceService.findAll(user);
        int start = Math.min((int) pageable.getOffset(), invoices.size());
        int end = Math.min(start + pageable.getPageSize(), invoices.size());
        return new PageImpl<>(invoices.subList(start, end), pageable, invoices.size());
    }


    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SALES_MANAGER','WAREHOUSE_MANAGER','STORE_MANAGER')")
    public Page<InvoiceDisplayDTO> getAllInvoices(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        List<InvoiceDisplayDTO> invoices = invoiceService.findAll();
        int start = Math.min((int) pageable.getOffset(), invoices.size());
        int end = Math.min(start + pageable.getPageSize(), invoices.size());
        return new PageImpl<>(invoices.subList(start, end), pageable, invoices.size());
    }


    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDisplayDTO> getById(@PathVariable Long id) {
        InvoiceDisplayDTO invoice = invoiceService.findById(id);
        if (invoice != null ) {
            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Invoice> create(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.save(invoice));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<Invoice> update(@PathVariable Long id, @RequestBody Invoice invoice) {
        Invoice existing = invoiceService.update(id);
        if (existing == null || !tenantValidator.validateTenantAccess(existing)) {
            return ResponseEntity.notFound().build();
        }
        invoice.setStatus(existing.getStatus());
        return ResponseEntity.ok(invoiceService.save(invoice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Invoice existing = invoiceService.getInvoice(id);
        if (existing != null && tenantValidator.validateTenantAccess(existing)) {
            invoiceService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
