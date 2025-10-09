package vector.StockManagement.services;


import vector.StockManagement.model.Transfer;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.TransferDTO;

import java.util.List;

public interface TransferService {
    List<Transfer> findAll();
    Transfer findById(Long id);
    Transfer save(Transfer transfer);
    void delete(Long id);

    Transfer process(TransferDTO transferDTO, User user);
}