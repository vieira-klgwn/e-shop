package vector.StockManagement.services.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.repositories.StockTransactionRepository;
import vector.StockManagement.services.StockTransactionService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public List<StockTransaction> findAll() {
        return stockTransactionRepository.findAll();
    }

    @Override
    public StockTransaction findById(Long id) {
        return stockTransactionRepository.findById(id).orElse(null);
    }

    @Override
    public StockTransaction save(StockTransaction stockTransaction) {
        return stockTransactionRepository.save(stockTransaction);
    }

    @Override
    public void delete(Long id) {
        stockTransactionRepository.deleteById(id);
    }
}