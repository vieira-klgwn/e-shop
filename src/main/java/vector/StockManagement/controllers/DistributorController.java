package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Distributor;
import vector.StockManagement.services.DistributorService;

import java.util.List;

@RestController
@RequestMapping("/distributors")
@RequiredArgsConstructor
public class DistributorController {

    private final DistributorService distributorService;


    @GetMapping
    public List<Distributor> getAll() {
        return distributorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Distributor> getById(@PathVariable Long id) {
        Distributor distributor = distributorService.findById(id);
        return distributor != null ? ResponseEntity.ok(distributor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Distributor create(@RequestBody Distributor distributor) {
        return distributorService.save(distributor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Distributor> update(@PathVariable Long id, @RequestBody Distributor distributor) {
        Distributor existing = distributorService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        distributor.setCompanyName(existing.getCompanyName());
        //add more updates here
        return ResponseEntity.ok(distributorService.save(distributor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        distributorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}