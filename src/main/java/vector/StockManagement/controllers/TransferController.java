package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Transfer;
import vector.StockManagement.model.dto.TransferDTO;
import vector.StockManagement.services.TransferService;

import java.util.List;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public List<Transfer> getAll() {
        return transferService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getById(@PathVariable Long id) {
        Transfer transfer = transferService.findById(id);
        return transfer != null ? ResponseEntity.ok(transfer) : ResponseEntity.notFound().build();
    }

    @PostMapping("/process")
    public ResponseEntity<TransferDTO> process(@RequestBody TransferDTO transferDTO) {
        return new ResponseEntity<>(transferService.process(transferDTO), HttpStatus.CREATED);
    }

    @PostMapping
    public Transfer create(@RequestBody Transfer transfer) {
        return transferService.save(transfer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transfer> update(@PathVariable Long id, @RequestBody Transfer transfer) {
        Transfer existing = transferService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        transfer.setStatus(existing.getStatus());
        return ResponseEntity.ok(transferService.save(transfer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transferService.delete(id);
        return ResponseEntity.noContent().build();
    }
}