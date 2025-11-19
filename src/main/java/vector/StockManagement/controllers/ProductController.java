package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.dto.PriceOfEachSizeDTO;
import vector.StockManagement.model.dto.ProductDTO;
import vector.StockManagement.model.dto.ProductDisplayDTO;
import vector.StockManagement.model.enums.ActivityCategory;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.ProductSizeRepository;
import vector.StockManagement.services.ActivityService;
import vector.StockManagement.services.ProductService;
import vector.StockManagement.services.impl.ProductServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductServiceImpl productServiceImpl;
    private final ProductSizeRepository productSizeRepository;
    private final ActivityService activityService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<ProductDisplayDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
//
//    @GetMapping("/factory")
//    public List<ProductDisplayDTO> getAllWarehouseProducts() {
//        PriceListLevel level = PriceListLevel.FACTORY;
//        return productService.findAll(level);
//    }

    @GetMapping("/store")
    public List<ProductDisplayDTO> getAllStoreProducts() {
        PriceListLevel level = PriceListLevel.DISTRIBUTOR;
        return productService.getAllStoreProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDisplayDTO> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new IllegalStateException("Product not found"));


        ProductDisplayDTO dto = new ProductDisplayDTO();
        dto.setProductCategory(product.getCategory());
        dto.setTenantName(product.getTenant().getName());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());
        dto.setName(product.getName());
        dto.setId(product.getId());
        dto.setSizes(product.getSizes());
        return ResponseEntity.ok(dto);
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
    public Product create(@AuthenticationPrincipal User user, @RequestBody ProductDTO productDTO) {
        Long tenantId = TenantContext.getTenantId();
        logger.info("Creating product: {} for tenant: {}", productDTO.getProductName(),tenantId);
        
        try {
            Product savedProduct = productService.save(productDTO);
            logger.info("Successfully created product: {} with ID: {} for tenant: {}", 
                       savedProduct.getName(), savedProduct.getId(), tenantId);
            activityService.createActivity(user,"Product Registered", ActivityCategory.PRODUCTS, "Product successfully registered for product: " + productDTO.getProductName() + "by Sales manager: " + user.getEmail());
            return savedProduct;
        } catch (Exception e) {
            logger.error("Failed to create product: {} for tenant: {} | Error: {}", 
                        productDTO.getProductName(), tenantId, e.getMessage(), e);
            activityService.createActivity(user,"Product Registration failed", ActivityCategory.PRODUCTS, "Product registration failed for product: " + productDTO.getProductName() + "by Sales manager: " + user.getEmail());
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

            // Store relative URL for frontend serving
            product.setImageUrl("/uploads/" + filename);
            logger.info("Image uploaded for product {}: {}", id, filename);  // Add logging
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


    @PutMapping("/{id}")
    public ResponseEntity<Product> setPrices(@PathVariable Long id, @RequestBody PriceOfEachSizeDTO priceOfEachSizeDTO) {
        Product product = productService.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        return ResponseEntity.ok(productService.setPricesForEachSize(product, priceOfEachSizeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

