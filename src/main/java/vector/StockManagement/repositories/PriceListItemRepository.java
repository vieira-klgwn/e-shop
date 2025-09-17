package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.enums.PriceListLevel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceListItem, Long> {
    
    List<PriceListItem> findByProductAndPriceListLevel(Product product, PriceListLevel level);
    
    @Query("SELECT pli FROM PriceListItem pli WHERE pli.product = :product AND pli.priceList.validFrom <= :date ORDER BY pli.priceList.validFrom DESC")
    List<PriceListItem> findHistoricalPricesForProduct(@Param("product") Product product, @Param("date") LocalDateTime date);
    
    @Query("SELECT pli FROM PriceListItem pli WHERE pli.product = :product AND pli.priceList.validFrom <= :date AND (pli.priceList.validTo IS NULL OR pli.priceList.validTo >= :date)")
    List<PriceListItem> findActivePricesForProduct(@Param("product") Product product, @Param("date") LocalDateTime date);

    List<PriceListItem> findByProductIdAndTenantId(Long productId, Long tenantId);
}
