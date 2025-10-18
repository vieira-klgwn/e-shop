package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vector.StockManagement.model.Sample;
import vector.StockManagement.services.SampleService;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {
    private final SampleService sampleService;

    public ResponseEntity<Sample> save(Sample sample) {
        return ResponseEntity.ok(sampleService.create(sample));
    }


    public ResponseEntity<Sample> update(Long id, Sample sample) {
        return ResponseEntity.ok(sampleService.update(id,sample));
    }


    public ResponseEntity<Sample> delete(Long id) {
        return ResponseEntity.ok(sampleService.delete(id));
    }

    public ResponseEntity<List<Sample>> findAll() {
        return ResponseEntity.ok(sampleService.findAll());
    }

    public ResponseEntity<Sample> findById(Long id) {
        return ResponseEntity.ok(sampleService.findById(id));
    }
}
