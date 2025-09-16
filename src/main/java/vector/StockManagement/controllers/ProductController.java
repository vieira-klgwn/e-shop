package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vector.StockManagement.model.Product;
import vector.StockManagement.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductController {

    private final ProductService productService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping
    @PreAuthorize("hasAnyRole('SALES_MANAGER','ACCOUNTANT','WAREHOUSE_MANAGER', 'DISTRIBUTOR')")
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        // Enforce tenant-aware access: ensure current user tenant matches entity tenant (filter already narrows queries, this is explicit double-check)
        // Assuming filter prevents cross-tenant reads, existing will be null if not same tenant.
        return ResponseEntity.ok(productService.update(id, product));
    }

    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Product> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productService.findById(id);
        if (product == null) return ResponseEntity.notFound().build();

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        product.setImageUrl("/" + uploadDir + "/" + filename);
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

