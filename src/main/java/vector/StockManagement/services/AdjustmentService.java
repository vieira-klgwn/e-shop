package vector.StockManagement.services;

import vector.StockManagement.model.Adjustment;

import java.util.List;

public interface AdjustmentService {
    List<Adjustment> findAll();
    Adjustment findById(Long id);
    Adjustment save(Adjustment adjustment);
    void delete(Long id);
}