package vector.StockManagement.services;


import vector.StockManagement.model.Notification;
import vector.StockManagement.model.Inventory;

import java.util.List;

public interface NotificationSerivice {


    List<Notification> findAll(Long userId);

    Notification findById(Long id);
    Notification save(Notification notification);
    void delete(Long id);
    void sendNotification(Notification notification);
    void checkAndNotifyLowStock();
}
