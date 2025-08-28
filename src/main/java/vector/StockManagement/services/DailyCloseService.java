package vector.StockManagement.services;


import vector.StockManagement.model.DailyClose;

import java.util.List;

public interface DailyCloseService {
    List<DailyClose> findAll();
    DailyClose findById(Long id);
    DailyClose save(DailyClose dailyClose);
    void delete(Long id);
}