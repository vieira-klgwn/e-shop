package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.OrderedProductSize;
import vector.StockManagement.model.User;
import vector.StockManagement.repositories.OrderedProductSizeRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.OrderedProductSizeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderedProductSizeServiceImpl implements OrderedProductSizeService {
    private final OrderedProductSizeRepository orderedProductSizeRepository;
    private final UserRepository userRepository;


    @Override
    public OrderedProductSize createOrderedProductSize(OrderedProductSize orderedProductSize) {
        return orderedProductSizeRepository.save(orderedProductSize);
    }

    @Override
    public OrderedProductSize findOrderedProductSizeById(Long id) {
        return orderedProductSizeRepository.findById(id).orElseThrow(() -> new RuntimeException("Ordered Product Size Not Found"));
    }

    @Override
    public List<OrderedProductSize> findOrderedProductSizeByCustomer(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
        return orderedProductSizeRepository.findAllByCustomer(user);
    }

    @Override
    public List<OrderedProductSize> findAllOrderedProductSizes(){
        return orderedProductSizeRepository.findAll();
    }
}
