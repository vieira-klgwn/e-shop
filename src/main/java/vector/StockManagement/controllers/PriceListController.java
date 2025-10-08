package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.services.PriceListService;

import java.util.List;

@RestController
@RequestMapping("/api/pricelists")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListController {

    private final PriceListService priceListService;

    @GetMapping
    public List<PriceList> getAll() {
        return priceListService.findAll();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SALES_MANAGER','MANAGING_DIRECTOR')")
    public ResponseEntity<PriceList> getById(@PathVariable Long id) {
        PriceList priceList = priceListService.findById(id);
        return priceList != null ? ResponseEntity.ok(priceList) : ResponseEntity.notFound().build();
    }





    @PostMapping
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public PriceList create(@RequestBody PriceList priceList) {
        return priceListService.save(priceList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceList> update(@PathVariable Long id, @RequestBody PriceList priceList) {
        PriceList existing = priceListService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        priceList.setValidTo(existing.getValidTo());
        return ResponseEntity.ok(priceListService.save(priceList));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}