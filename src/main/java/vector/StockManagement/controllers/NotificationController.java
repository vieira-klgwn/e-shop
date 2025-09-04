package vector.StockManagement.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Notification;
import vector.StockManagement.services.NotificationSerivice;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationSerivice notificationSerivice;

    @GetMapping
    public List<Notification> getAll() {
        return notificationSerivice.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationSerivice.findById(id);
        return notification != null ? ResponseEntity.ok(notification) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Notification create(@RequestBody Notification notification) {
        return notificationSerivice.save(notification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> update(@PathVariable Long id, @RequestBody Notification notification) {
        Notification existing = notificationSerivice.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        notification.setMessage(existing.getMessage());
        //add more updates here
        return ResponseEntity.ok(notificationSerivice.save(notification));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationSerivice.delete(id);
        return ResponseEntity.noContent().build();
    }
}
