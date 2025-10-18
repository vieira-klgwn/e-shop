package vector.StockManagement.services;

import vector.StockManagement.model.Sample;
import vector.StockManagement.model.StockTransaction;

import java.util.List;

public interface ReportService {
    Long findById(Long id);
    List<StockTransaction> findAll();
    Long update(Long id);
    Long delete(Long id);
    Sample create(StockTransaction stockTransaction);

}
