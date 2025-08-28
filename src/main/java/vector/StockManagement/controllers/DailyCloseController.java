package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.DailyClose;
import vector.StockManagement.services.DailyCloseService;

import java.util.List;

@RestController
@RequestMapping("/dailycloses")
@RequiredArgsConstructor
public class DailyCloseController {
    private final DailyCloseService dailyCloseService;


    @GetMapping
    public List<DailyClose> getAll() {
        return dailyCloseService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyClose> getById(@PathVariable Long id) {
        DailyClose dailyClose = dailyCloseService.findById(id);
        return dailyClose != null ? ResponseEntity.ok(dailyClose) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public DailyClose create(@RequestBody DailyClose dailyClose) {
        return dailyCloseService.save(dailyClose);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyClose> update(@PathVariable Long id, @RequestBody DailyClose dailyClose) {
        DailyClose existing = dailyCloseService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        dailyClose.setStatus(existing.getStatus());
        //add more adjustements here
        return ResponseEntity.ok(dailyCloseService.save(dailyClose));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dailyCloseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}