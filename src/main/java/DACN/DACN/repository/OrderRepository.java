package DACN.DACN.repository;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByIdDesc(User user);  // Tìm đơn hàng theo người dùng
    List<Order> findAllByOrderByIdDesc();
}

