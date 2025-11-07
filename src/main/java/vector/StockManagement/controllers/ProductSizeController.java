package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.model.dto.ProductSizeDTO;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.ProductSizeRepository;
import vector.StockManagement.services.ProductSizeService;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class ProductSizeController {
    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;
    private final ProductSizeService productSizeService;

    @PutMapping("/updateQuantity")
    public ResponseEntity<ProductSize> updateProductSize(@RequestBody ProductSizeDTO productSizeDto){
        return ResponseEntity.ok(productSizeService.updateQtyOnHand(productSizeDto.getProductId(), productSizeDto.getProductSize(), productSizeDto.getQtyOnHand()));
    }

    @PutMapping("/updatePrice")
    public  ResponseEntity<ProductSize> updateProductPrice(@RequestBody ProductSizeDTO productSizeDto){
        return ResponseEntity.ok(productSizeService.updatePriceBySize(productSizeDto.getProductId(), productSizeDto.getProductSize(), productSizeDto.getPrice()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSize> findProductSizeById(@PathVariable Long id){
        return ResponseEntity.ok(productSizeRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found")));
    }
}
