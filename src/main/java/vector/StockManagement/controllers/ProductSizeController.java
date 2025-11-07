package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.ProductSizeDTO;
import vector.StockManagement.model.enums.ActivityCategory;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.ProductSizeRepository;
import vector.StockManagement.services.ActivityService;
import vector.StockManagement.services.ProductSizeService;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class ProductSizeController {
    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;
    private final ProductSizeService productSizeService;
    private final ActivityService activityService;

    @PutMapping("/updateQuantity")
    public ResponseEntity<ProductSize> updateProductSize(@AuthenticationPrincipal User user, @RequestBody ProductSizeDTO productSizeDto){
        Product product = productRepository.findById(productSizeDto.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));
        activityService.createActivity(user, "Product Quantity Updated", ActivityCategory.PRODUCTS,"Product Quantity Updated for product: " + product.getName() +" with size: "+ productSizeDto.getProductSize());
        ProductSize size = productSizeService.updateQtyOnHand(productSizeDto.getProductId(), productSizeDto.getProductSize(), productSizeDto.getQtyOnHand());
        return ResponseEntity.ok(size);
    }

    @PutMapping("/updatePrice")
    public  ResponseEntity<ProductSize> updateProductPrice(@AuthenticationPrincipal User user, @RequestBody ProductSizeDTO productSizeDto){
        Product product = productRepository.findById(productSizeDto.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));
        ProductSize size = productSizeService.updatePriceBySize(productSizeDto.getProductId(), productSizeDto.getProductSize(), productSizeDto.getPrice());
        activityService.createActivity(user, "Product Quantity Updated", ActivityCategory.PRODUCTS,"Product Quantity Updated for product: " + product.getName() +" with size: "+ productSizeDto.getProductSize());
        return ResponseEntity.ok(size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSize> findProductSizeById(@PathVariable Long id){
        return ResponseEntity.ok(productSizeRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found")));
    }
}
