package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.DailyClose;
import vector.StockManagement.repositories.DailyCloseRepository;
import vector.StockManagement.services.DailyCloseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyCloseServiceImpl implements DailyCloseService {

    private final DailyCloseRepository dailyCloseRepository;

    @Override
    public List<DailyClose> findAll() {
        return dailyCloseRepository.findAll();
    }

    @Override
    public DailyClose findById(Long id) {
        return dailyCloseRepository.findById(id).orElse(null);
    }

    @Override
    public DailyClose save(DailyClose dailyClose) {
        return dailyCloseRepository.save(dailyClose);
    }

    @Override
    public void delete(Long id) {
        dailyCloseRepository.deleteById(id);
    }
}