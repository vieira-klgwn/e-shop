package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    List<PriceList> findByTenantId(Long tenantId);
    Optional<PriceList> findByIdAndTenantId(Long id, Long tenantId);
    void deleteByIdAndTenantId(Long id, Long tenantId);
}
