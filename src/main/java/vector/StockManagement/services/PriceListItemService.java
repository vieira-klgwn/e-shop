package vector.StockManagement.services;

import vector.StockManagement.model.PriceListItem;

import java.util.List;

public interface PriceListItemService {
    List<PriceListItem> findAll();
    PriceListItem findById(Long id);
    PriceListItem save(PriceListItem priceListItem);
    void delete(Long id);
}