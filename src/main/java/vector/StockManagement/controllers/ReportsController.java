//package vector.StockManagement.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import vector.StockManagement.repositories.InventoryRepository;
//import vector.StockManagement.repositories.OrderRepository;
//import vector.StockManagement.repositories.PaymentRepository;
//import vector.StockManagement.repositories.ProductRepository;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/reports")
//@RequiredArgsConstructor
//public class ReportsController {
//
//    private final OrderRepository orderRepository;
//    private final InventoryRepository inventoryRepository;
//    private final PaymentRepository paymentRepository;
//    private final ProductRepository productRepository;
//
//    @GetMapping("/sales")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'ACCOUNTANT','MANAGING_DIRECTOR')")
//    public ResponseEntity<Map<String, Object>> getSalesReport(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
//
//        Map<String, Object> report = new HashMap<>();
//        report.put("totalSales", orderRepository.sumOrderAmountBetween(from, to));
//        report.put("totalOrders", orderRepository.countOrdersBetween(from, to));
//        report.put("period", Map.of("from", from, "to", to));
//
//        return ResponseEntity.ok(report);
//    }
//
//    @GetMapping("/inventory")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER', 'SALES_MANAGER','MANAGING_DIRECTOR')")
//    public ResponseEntity<Map<String, Object>> getInventoryReport() {
//        Map<String, Object> report = new HashMap<>();
//
//        long totalProducts = productRepository.count();
//        long totalInventoryItems = inventoryRepository.count();
//
//        report.put("totalProducts", totalProducts);
//        report.put("totalInventoryItems", totalInventoryItems);
//        report.put("generatedAt", LocalDateTime.now());
//
//        return ResponseEntity.ok(report);
//    }
//
//    @GetMapping("/financials")
//    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT','MANAGING_DIRECTOR')")
//    public ResponseEntity<Map<String, Object>> getFinancialReport(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
//
//        Map<String, Object> report = new HashMap<>();
//
//        Long totalSales = orderRepository.sumOrderAmountBetween(from, to);
//        Long totalPayments = paymentRepository.sumPaymentAmountBetween(from, to);
//
//        report.put("totalSales", totalSales);
//        report.put("totalPayments", totalPayments);
//        report.put("outstandingAmount", (totalSales != null ? totalSales : 0L) - (totalPayments != null ? totalPayments : 0L));
//        report.put("period", Map.of("from", from, "to", to));
//        report.put("generatedAt", LocalDateTime.now());
//
//        return ResponseEntity.ok(report);
//    }
//}
