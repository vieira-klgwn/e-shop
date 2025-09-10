package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.config.TenantAwareValidator;
import vector.StockManagement.model.Invoice;
import vector.StockManagement.services.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final TenantAwareValidator tenantValidator;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SALES_MANAGER')")
    public Page<Invoice> getAll(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Invoice> invoices = invoiceService.findAll();
        int start = Math.min((int) pageable.getOffset(), invoices.size());
        int end = Math.min(start + pageable.getPageSize(), invoices.size());
        return new PageImpl<>(invoices.subList(start, end), pageable, invoices.size());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SALES_MANAGER')")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        Invoice invoice = invoiceService.findById(id);
        if (invoice != null && tenantValidator.validateTenantAccess(invoice)) {
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
        Invoice existing = invoiceService.findById(id);
        if (existing == null || !tenantValidator.validateTenantAccess(existing)) {
            return ResponseEntity.notFound().build();
        }
        invoice.setStatus(existing.getStatus());
        return ResponseEntity.ok(invoiceService.save(invoice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Invoice existing = invoiceService.findById(id);
        if (existing != null && tenantValidator.validateTenantAccess(existing)) {
            invoiceService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
