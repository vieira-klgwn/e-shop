package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.CreateProductRequest;
import vector.StockManagement.model.Product;
import vector.StockManagement.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }


    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public Product create(@RequestBody CreateProductRequest createProductRequest) {
        return productService.save(createProductRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public ResponseEntity<Product> update(@PathVariable Long id, CreateProductRequest createProductRequest) {
        Product existing = productService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setName(createProductRequest.getName());
        //add more updates here
        return ResponseEntity.ok(productService.update(id, createProductRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}