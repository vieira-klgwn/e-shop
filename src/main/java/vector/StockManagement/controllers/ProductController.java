package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.dto.ProductDisplayDTO;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.services.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping("/factory")
    public List<ProductDisplayDTO> getAllWarehouseProducts() {
        PriceListLevel level = PriceListLevel.FACTORY;
        return productService.findAll(level);
    }

    @GetMapping("/store")
    public List<ProductDisplayDTO> getAllStoreProducts() {
        PriceListLevel level = PriceListLevel.DISTRIBUTOR;
        return productService.getAllStoreProducts();
    }

    @GetMapping("/{id}/factory")
    public ResponseEntity<ProductDisplayDTO> getById(@PathVariable Long id) {
        PriceListLevel level = PriceListLevel.FACTORY;
        ProductDisplayDTO dto = productService.findById1(id, level);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

//    @GetMapping("/store")
//    public ResponseEntity<List<Product>> getByStore(@RequestParam String store) {
//        return ResponseEntity.ok(productService.getAllStoreProducts());
//    }

    @GetMapping("/{id}/prices")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'ADMIN','DISTRIBUTOR','WAREHOUSE_MANAGER','RETAILER','MANAGIND_DIRECTOR')")
    public ResponseEntity<PriceDisplayDTO> getProductPrices(@PathVariable Long id) {
        Long tenantId = TenantContext.getTenantId();
        return ResponseEntity.ok(productService.getProductPrices(id, tenantId));
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        Long tenantId = TenantContext.getTenantId();
        logger.info("Creating product: {} for tenant: {}", product.getName(), tenantId);
        
        try {
            Product savedProduct = productService.save(product);
            logger.info("Successfully created product: {} with ID: {} for tenant: {}", 
                       savedProduct.getName(), savedProduct.getId(), tenantId);
            return savedProduct;
        } catch (Exception e) {
            logger.error("Failed to create product: {} for tenant: {} | Error: {}", 
                        product.getName(), tenantId, e.getMessage(), e);
            throw e;
        }
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
//    public ResponseEntity<ProductDisplayDTO> update(@PathVariable Long id, @RequestBody Product product) {
//        Product existing = productService.findById(id);
//        if (existing == null) return ResponseEntity.notFound().build();
//        // Enforce tenant-aware access: ensure current user tenant matches entity tenant (filter already narrows queries, this is explicit double-check)
//        // Assuming filter prevents cross-tenant reads, existing will be null if not same tenant.
//        return ResponseEntity.ok(productService.update(id, product));
//    }

    @PostMapping("/{id}/upload-image")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Product> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalStateException("File is empty");
        }
        if (!isValidImage(file)) {
            throw new IllegalStateException("Invalid file type. Only JPG, PNG, GIF allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) {  // 5MB limit
            throw new IllegalStateException("File too large (max 5MB)");
        }

        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String original = file.getOriginalFilename();
            String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
            String filename = UUID.randomUUID() + ext.toLowerCase();
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            product.setImageUrl("/" + uploadDir + "/" + filename);  // Relative URL for frontend serving
            return ResponseEntity.ok(productService.update(id, product));
        } catch (IOException e) {
            logger.error("Failed to upload image for product ID {}: {}", id, e.getMessage());
            throw new IllegalStateException("Failed to upload image: " + e.getMessage());
        }
    }

    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

