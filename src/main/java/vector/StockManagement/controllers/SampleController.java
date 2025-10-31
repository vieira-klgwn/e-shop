//package vector.StockManagement.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import vector.StockManagement.model.Sample;
//import vector.StockManagement.model.User;
//import vector.StockManagement.model.dto.CreateSampleRequest;
//import vector.StockManagement.model.dto.SampleResponse;
//import vector.StockManagement.repositories.SampleRepository;
//import vector.StockManagement.services.SampleService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/samples")
//@RequiredArgsConstructor
//public class SampleController {
//    private final SampleService sampleService;
//    private final SampleRepository sampleRepository;
//
//    @PostMapping
//    public ResponseEntity<Sample> save(@RequestBody CreateSampleRequest request, @AuthenticationPrincipal User user) {
//        return ResponseEntity.ok(sampleService.create(request,user));
//    }
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Sample> update(Long id, Sample sample) {
//        return ResponseEntity.ok(sampleService.update(id,sample));
//    }
//
//    @PutMapping("/fulfill/{id}")
//    public ResponseEntity<Sample> fulfill(@PathVariable Long id) {
//        Sample sample = sampleRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Sample not found"));
//        return ResponseEntity.ok(sampleService.fullfillSample(sample));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Sample> delete(@PathVariable Long id) {
//        return ResponseEntity.ok(sampleService.delete(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<SampleResponse>> findAll() {
//        return ResponseEntity.ok(sampleService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SampleResponse> findById(@PathVariable Long id) {
//        return ResponseEntity.ok(sampleService.findById(id));
//    }
//}
