//package vector.StockManagement.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import vector.StockManagement.model.StockTransaction;
//import vector.StockManagement.services.StockTransactionService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/stocktransactions")
//@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//public class StockTransactionController {
//
//    private final StockTransactionService stockTransactionService;
//
//    @GetMapping
//    public List<StockTransaction> getAll() {
//        return stockTransactionService.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<StockTransaction> getById(@PathVariable Long id) {
//        StockTransaction stockTransaction = stockTransactionService.findById(id);
//        return stockTransaction != null ? ResponseEntity.ok(stockTransaction) : ResponseEntity.notFound().build();
//    }
//
//    @PostMapping
//    public StockTransaction create(@RequestBody StockTransaction stockTransaction) {
//        return stockTransactionService.save(stockTransaction);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<StockTransaction> update(@PathVariable Long id, @RequestBody StockTransaction stockTransaction) {
//        StockTransaction existing = stockTransactionService.findById(id);
//        if (existing == null) return ResponseEntity.notFound().build();
//        stockTransaction.setQty(existing.getQty());
//        // add more updates here
//        return ResponseEntity.ok(stockTransactionService.save(stockTransaction));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        stockTransactionService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}