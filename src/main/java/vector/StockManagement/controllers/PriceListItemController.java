package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.services.PriceListItemService;

import java.util.List;

@RestController
@RequestMapping("/pricelistitems")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListItemController {

    private final PriceListItemService priceListItemService;

    @GetMapping
    public List<PriceListItem> getAll() {
        return priceListItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceListItem> getById(@PathVariable Long id) {
        PriceListItem priceListItem = priceListItemService.findById(id);
        return priceListItem != null ? ResponseEntity.ok(priceListItem) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public PriceListItem create(@RequestBody PriceListItem priceListItem) {
        return priceListItemService.save(priceListItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceListItem> update(@PathVariable Long id, @RequestBody PriceListItem priceListItem) {
        PriceListItem existing = priceListItemService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        priceListItem.setDiscounts(existing.getDiscounts());
        priceListItem.setIsActive(existing.getIsActive());
        //add more updates here
        return ResponseEntity.ok(priceListItemService.save(priceListItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceListItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}