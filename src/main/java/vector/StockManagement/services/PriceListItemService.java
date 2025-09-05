package vector.StockManagement.services;

import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.dto.PriceListItemDTO;

import java.util.List;

public interface PriceListItemService {
    List<PriceListItem> findAll();
    PriceListItem findById(Long id);
    PriceListItem save(PriceListItemDTO priceListItemDTO);
    void delete(Long id);
}