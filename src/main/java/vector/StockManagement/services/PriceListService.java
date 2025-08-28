package vector.StockManagement.services;

import vector.StockManagement.model.PriceList;

import java.util.List;

public interface PriceListService {
    List<PriceList> findAll();
    PriceList findById(Long id);
    PriceList save(PriceList priceList);
    void delete(Long id);
}