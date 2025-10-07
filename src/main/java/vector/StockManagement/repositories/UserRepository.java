package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional <User> findByEmail(String email);


    User findByIdAndRole_Accountant(Long id);

    List<User> findByDistributor_Id(Long distributorId);

    List<User> findByDistributor_IdAndRole_AccountantAtStore(Long distributorId);

    List<User> findByDistributor_IdAndRole_StoreManage(Long distributorId);
}
