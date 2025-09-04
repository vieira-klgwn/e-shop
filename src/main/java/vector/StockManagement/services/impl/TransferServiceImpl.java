package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Transfer;
import vector.StockManagement.repositories.TransferRepository;
import vector.StockManagement.services.TransferService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;

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


        return transferRepository.save(transfer);
    }

    @Override
    public void delete(Long id) {
        transferRepository.deleteById(id);
    }
}