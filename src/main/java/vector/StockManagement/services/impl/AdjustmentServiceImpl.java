package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Adjustment;
import vector.StockManagement.repositories.AdjustmentRepository;
import vector.StockManagement.services.AdjustmentService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;

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
        return adjustmentRepository.save(adjustment);
    }

    @Override
    public void delete(Long id) {
        adjustmentRepository.deleteById(id);
    }
}