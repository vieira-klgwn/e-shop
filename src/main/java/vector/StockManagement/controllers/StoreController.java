//package vector.StockManagement.controllers;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import vector.StockManagement.auth.AuthenticationResponse;
//import vector.StockManagement.model.Store;
//import vector.StockManagement.model.dto.StoreDTO;
//import vector.StockManagement.services.StoreService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("api/stores")
//@RequiredArgsConstructor
//public class StoreController {
//
//
//    private final StoreService storeService;
//
//    @GetMapping
//    public List<Store> getAll() {
//        return storeService.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Store> getById(@PathVariable Long id) {
//        Store store = storeService.findById(id);
//        return store != null ? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<AuthenticationResponse> create(@RequestBody StoreDTO storeDTO) {
//        return ResponseEntity.ok(storeService.save(storeDTO));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Store> update(@PathVariable Long id, @RequestBody Store store) {
//        return ResponseEntity.ok(storeService.update(id, store));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        storeService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}