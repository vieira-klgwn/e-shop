package vector.StockManagement.services;

import vector.StockManagement.model.Distributor;
import java.util.List;

public interface DistributorService {
    List<Distributor> findAll();
    Distributor findById(Long id);
    Distributor save(Distributor distributor);
    void delete(Long id);
}