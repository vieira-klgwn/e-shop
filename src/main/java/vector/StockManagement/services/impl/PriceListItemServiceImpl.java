package vector.StockManagement.services.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.repositories.PriceListItemRepository;
import vector.StockManagement.services.PriceListItemService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListItemServiceImpl implements PriceListItemService {

    private final PriceListItemRepository priceListItemRepository;

    @Override
    public List<PriceListItem> findAll() {
        return priceListItemRepository.findAll();
    }

    @Override
    public PriceListItem findById(Long id) {
        return priceListItemRepository.findById(id).orElse(null);
    }

    @Override
    public PriceListItem save(PriceListItem priceListItem) {
        return priceListItemRepository.save(priceListItem);
    }

    @Override
    public void delete(Long id) {
        priceListItemRepository.deleteById(id);
    }
}