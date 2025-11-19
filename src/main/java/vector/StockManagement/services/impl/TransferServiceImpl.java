package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.AdjustOrderDTO;
import vector.StockManagement.model.dto.TransferDTO;
import vector.StockManagement.model.enums.*;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.ProductService;
import vector.StockManagement.services.StockTransactionService;
import vector.StockManagement.services.TransferService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final InventoryRepository inventoryRepository;
    private final StockTransactionService stockTransactionService;
    private final OrderRepository orderRepository;
    private final OrderServiceImpl orderServiceImpl;
    private final InvoiceRepository invoiceRepository;
    private final TenantRepository tenantRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderedProductSizeRepository orderedProductSizeRepository;

    @Override
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Override
    public Transfer findById(Long id) {
        return transferRepository.findById(id).orElse(null);
    }

    @Override
    public Transfer save(Transfer transfer) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            transfer.setTenant(user.getTenant());
        }
        return transferRepository.save(transfer);
    }

    @Transactional
    public Transfer completeTransfer(Long transferId) {
        Transfer transfer = findById(transferId);
        if (transfer == null || transfer.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("Transfer not found or already completed");
        }

        // Update inventory at source location (remove stock)
        Inventory sourceInventory = inventoryRepository.findByProductAndLocationTypeAndLocationId(
                transfer.getProduct(), transfer.getFromLocationType(), transfer.getFromLocationId());
        if (sourceInventory != null && sourceInventory.getQtyOnHand() >= transfer.getQty()) {
            sourceInventory.removeStock(transfer.getQty());
            inventoryRepository.save(sourceInventory);
        } else {
            throw new RuntimeException("Insufficient stock at source location");
        }

        // Update inventory at destination location (add stock)
        Inventory destInventory = inventoryRepository.findByProductAndLocationTypeAndLocationId(
                transfer.getProduct(), transfer.getToLocationType(), transfer.getToLocationId());
        if (destInventory == null) {
            // Create new inventory record if doesn't exist
            destInventory = new Inventory(transfer.getToLocationType(), transfer.getToLocationId(), 
                    transfer.getProduct(), transfer.getTenant());
        }
        destInventory.addStock(transfer.getQty()); //removed sourceInventory != null ? sourceInventory.getAvgUnitCost() : null
        inventoryRepository.save(destInventory);

        // Log stock transactions
        logStockTransaction(transfer, StockTransactionType.TRANSFER_OUT, transfer.getFromLocationType(), transfer.getFromLocationId(), -transfer.getQty());
        logStockTransaction(transfer, StockTransactionType.TRANSFER_IN, transfer.getToLocationType(), transfer.getToLocationId(), transfer.getQty());

        // Update transfer status
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    private void logStockTransaction(Transfer transfer, StockTransactionType type, LocationType locationType, Long locationId, Integer qty) {
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(transfer.getProduct());
        transaction.setType(type);
        transaction.setLocationType(locationType);
        transaction.setLocationId(locationId);
        transaction.setQty(qty);
        transaction.setReferenceType("TRANSFER");
        transaction.setReferenceId(transfer.getId());
        transaction.setTenant(transfer.getTenant());
        transaction.setTransactionDate(LocalDateTime.now());
        stockTransactionService.save(transaction);
    }

    @Override
    public void delete(Long id) {
        transferRepository.deleteById(id);
    }

    @Transactional  // Ensures single transaction; flushes only at commit (avoids mid-iteration flushes)
    public void adjustOrder(Long id, TransferDTO transferDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.FULFILLED){
            throw new RuntimeException("Order can not be transferred when it is not fulfilled");
        }

        boolean isTransfered = false;

        // Partial Quantity Block (apply first, as it changes quantities used in price calcs)
        if (transferDTO.getPartialQtys() != null && !transferDTO.getPartialQtys().isEmpty()) {
            isTransfered = true;
            Map<Long, Long> partialQtys = transferDTO.getPartialQtys();
            for (Map.Entry<Long, Long> partialQty : partialQtys.entrySet()) {
                if (partialQty.getValue() < 0) {
                    throw new RuntimeException("Quantity cannot be negative: " + partialQty.getValue());
                }
                // Find and update the specific size (no full loop over all lines per partial for efficiency)
                boolean found = false;
                for (OrderLine line : order.getOrderLines()) {  // Direct iteration safe now (no mid-saves)
                    Long calculatedTotal = 0L;
                    for (OrderedProductSize size : line.getProductSizes()) {  // Direct iteration
                        if (size.getId().equals(partialQty.getKey())) {
                            if (partialQty.getValue() > size.getQuantityInStock()) {
                                throw new RuntimeException("The updated quantity you want to add, is greater than the ordered quantity before");
                            }

                            // Note: This adds the difference to stock (assuming partialQty is the new total qty, so delta = new - old)

                            size.setQuantityInStock(partialQty.getValue().intValue());
                            orderedProductSizeRepository.save(size);


                            found = true;
                            break;
                        }


                        //update the size total price and add it to the placeholder of the linetotal
                        calculatedTotal += size.getPrice() * partialQty.getValue();
                    }

                    if (found) {
                        line.setLineTotal(calculatedTotal);
                        orderLineRepository.save(line);
                        break;
                    }
                }
                if (!found) {
                    // Optional: Log warning - partial for non-existent size
                    log.warn("Partial for non-existent size: {}", partialQty.getKey());
                }
            }
        }




        // Final: Recalc orderAmount from all lineTotals (simple sum)
        Long totalAmount = order.getOrderLines().stream()
                .mapToLong(line -> line.getLineTotal() != null ? line.getLineTotal() : 0L)
                .sum();

        // Apply overall adjustments if present (e.g., customerDiscount or priceAdjustment)
        // Assuming these are absolute deductions/additions to total

        order.setOrderAmount(totalAmount < 0 ? 0L : totalAmount);  // Clamp to >=0


        if (isTransfered){
            order.setStatus(OrderStatus.TRANSFERRED);
        }

        // Single save at end: Cascades to OrderLines/ProductSizes (no intermediate flushes)
        orderRepository.save(order);  // This triggers one flush/commit for all changes
    }
    @Override
    @Transactional
    public Transfer process(TransferDTO transferDTO, User user) {
        Order order = orderRepository.getOrderById(transferDTO.getOrderId());
        Tenant tenant = tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found"));
        Transfer transfer = new Transfer();
        transfer.setQty(transferDTO.getQuantityToTransfer());
        transfer.setTenant(tenant);
        transfer.setNotes(transferDTO.getReason());

        adjustOrder(order.getId(), transferDTO);

        for (Invoice invoice: order.getCreatedBy().getInvoices()){
            if (invoice.getOrder()==order){
                invoice.setInvoiceAmount(order.getOrderAmount());
                invoiceRepository.saveAndFlush(invoice);
            }
        }


        transfer.setStatus(TransferStatus.PENDING);
        transfer.setQty(transferDTO.getQuantityToTransfer());
        transfer.setCreatedBy(order.getCreatedBy());
        transfer.setCompletedAt(LocalDateTime.now());
        transfer.setOrderId(transferDTO.getOrderId());
        transferRepository.saveAndFlush(transfer);
        return transfer;
    }

}
