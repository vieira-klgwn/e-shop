package vector.StockManagement.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.model.enums.StockTransactionType;
import vector.StockManagement.repositories.AdjustmentRepository;
import vector.StockManagement.repositories.InventoryRepository;
import vector.StockManagement.services.AdjustmentService;
import vector.StockManagement.services.StockTransactionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final InventoryRepository inventoryRepository;
    private final StockTransactionService stockTransactionService;

    @Override
    public List<Adjustment> findAll() {
        return adjustmentRepository.findAll();
    }

    @Override
    public Adjustment findById(Long id) {
        return adjustmentRepository.findById(id).orElse(null);
    }

    @Override
    public Adjustment save(Adjustment adjustment) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            adjustment.setTenant(user.getTenant());
        }
        return adjustmentRepository.save(adjustment);
    }

    @Transactional
    public Adjustment processAdjustment(Long adjustmentId) {
        Adjustment adjustment = findById(adjustmentId);
        if (adjustment == null) {
            throw new RuntimeException("Adjustment not found");
        }

        // Find the inventory record
        Inventory inventory = inventoryRepository.findByProductAndLocationTypeAndLocationId(
                adjustment.getProduct(), adjustment.getLocationType(), adjustment.getLocationId());
        
        if (inventory == null) {
            throw new RuntimeException("Inventory record not found for this product and location");
        }

        // Calculate the adjustment quantity (difference between new count and current stock)
        Integer currentQty = inventory.getQtyOnHand();
        Integer adjustmentQty = adjustment.getNewQty() - currentQty;

        // Update inventory
        if (adjustmentQty > 0) {
            // Positive adjustment (add stock)
            inventory.addStock(adjustmentQty); //removed , adjustment.getUnitCost()
        } else if (adjustmentQty < 0) {
            // Negative adjustment (remove stock)
            inventory.removeStock(Math.abs(adjustmentQty));
        }
        
        inventoryRepository.save(inventory);

        // Log stock transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(adjustment.getProduct());
        transaction.setType(adjustmentQty > 0 ? StockTransactionType.ADJUSTMENT_IN : StockTransactionType.ADJUSTMENT_OUT);
        transaction.setLocationType(adjustment.getLocationType());
        transaction.setLocationId(adjustment.getLocationId());
        transaction.setQty(adjustmentQty);
        transaction.setReferenceType("ADJUSTMENT");
        transaction.setReferenceId(adjustment.getId());
        transaction.setTenant(adjustment.getTenant());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setNotes("Adjustment: " + adjustment.getReason() + ". " + adjustment.getNotes());
        stockTransactionService.save(transaction);

        // Update adjustment record
        adjustment.setProcessedAt(LocalDateTime.now());
        adjustment.setOldQty(currentQty);
        
        return adjustmentRepository.save(adjustment);
    }

    @Override
    public void delete(Long id) {
        adjustmentRepository.deleteById(id);
    }
}
