//package vector.StockManagement.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import vector.StockManagement.model.Adjustment;
//import vector.StockManagement.services.AdjustmentService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/adjustments")
//public class AdjustmentController {
//
//    @Autowired
//    private AdjustmentService adjustmentService;
//
//    @GetMapping
//    public List<Adjustment> getAll() {
//        return adjustmentService.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Adjustment> getById(@PathVariable Long id) {
//        Adjustment adjustment = adjustmentService.findById(id);
//        return adjustment != null ? ResponseEntity.ok(adjustment) : ResponseEntity.notFound().build();
//    }
//
//    @PostMapping
//    public Adjustment create(@RequestBody Adjustment adjustment) {
//        return adjustmentService.save(adjustment);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Adjustment> update(@PathVariable Long id, @RequestBody Adjustment adjustment) {
//        Adjustment existing = adjustmentService.findById(id);
//        if (existing == null) return ResponseEntity.notFound().build();
//        adjustment.setReason(adjustment.getReason());
//        //add more updates here
//        return ResponseEntity.ok(adjustmentService.save(adjustment));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        adjustmentService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}