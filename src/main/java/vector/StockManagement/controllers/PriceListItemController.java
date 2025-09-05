package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.dto.PriceListItemDTO;
import vector.StockManagement.repositories.PriceListRepository;
import vector.StockManagement.services.PriceListItemService;

import java.util.List;

@RestController
@RequestMapping("/api/pricelistitems")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListItemController {

    private final PriceListItemService priceListItemService;
    private final PriceListRepository priceListRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SALES_MANAGER')")
    public List<PriceListItem> getAll() {
        return priceListItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceListItem> getById(@PathVariable Long id) {
        PriceListItem priceListItem = priceListItemService.findById(id);
        return priceListItem != null ? ResponseEntity.ok(priceListItem) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SALES_MANAGER')")
    public PriceListItem create(@RequestBody PriceListItemDTO priceListItemDTO) {
        return priceListItemService.save(priceListItemDTO);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<PriceListItem> update(@PathVariable Long id, @RequestBody PriceListItemDTO priceListItemDTO) {
//        PriceListItem existing = priceListItemService.findById(id);
//        if (existing == null) return ResponseEntity.notFound().build();
//        existing.setProduct(priceListItemDTO.getProduct());
//        existing.setPriceList(priceListItemDTO.getPriceList());
//        //add more updates here
//        return ResponseEntity.ok(priceListRepository.save(existing));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceListItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}