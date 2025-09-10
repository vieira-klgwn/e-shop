package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.model.User;
import vector.StockManagement.repositories.StockTransactionRepository;
import vector.StockManagement.services.StockTransactionService;

import java.time.LocalDateTime;
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
        // Ensure tenant is set from current user if not already set
        if (stockTransaction.getTenant() == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User user && user.getTenant() != null) {
                stockTransaction.setTenant(user.getTenant());
            }
        }
        
        if (stockTransaction.getTransactionDate() == null) {
            stockTransaction.setTransactionDate(LocalDateTime.now());
        }
        
        return stockTransactionRepository.save(stockTransaction);
    }

    @Override
    public void delete(Long id) {
        stockTransactionRepository.deleteById(id);
    }

    @Override
    public List<StockTransaction> findByProduct(Product product) {
        return stockTransactionRepository.findByProduct(product);
    }

    @Override
    public List<StockTransaction> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return stockTransactionRepository.findByTransactionDateBetween(from, to);
    }
}
