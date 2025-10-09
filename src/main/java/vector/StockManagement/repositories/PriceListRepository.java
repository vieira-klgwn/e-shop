package vector.StockManagement.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.enums.PriceListLevel;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    List<PriceList> findByTenantId(Long tenantId);
    Optional<PriceList> findByIdAndTenantId(Long id, Long tenantId);
    void deleteByIdAndTenantId(Long id, Long tenantId);

    List<PriceList> findByLevel(@NotNull PriceListLevel level);

    List<PriceList> findByLevelAndIsActive(@NotNull PriceListLevel level, Boolean isActive);

    List<PriceList> findByLevelAndIsActiveAndTenantId(PriceListLevel level, Boolean isActive, Long tenantId);

    List<PriceList> findByProductAndLevel(Product product, @NotNull PriceListLevel level);
}
