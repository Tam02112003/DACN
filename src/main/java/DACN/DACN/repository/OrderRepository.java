package DACN.DACN.repository;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderStatus; // Nhớ import OrderStatus nếu bạn muốn tìm theo trạng thái
import DACN.DACN.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserOrderByIdDesc(User user);  // Tìm đơn hàng theo người dùng
    List<Order> findAllByOrderByIdDesc();
    Order findByTransactionCode(String transactionCode);

    // Phương thức tìm kiếm theo các tiêu chí
    @Query("SELECT o FROM Order o WHERE " +
            "(:orderId IS NULL OR o.id = :orderId) AND " +
            "(:customerName IS NULL OR o.customerName LIKE %:customerName%) AND " +
            "(:phone IS NULL OR o.phone = :phone) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:orderDate IS NULL OR DATE(o.orderDate) = DATE(:orderDate)) AND " +
            "(:estimatedDeliveryDate IS NULL OR DATE(o.estimatedDeliveryDate) = DATE(:estimatedDeliveryDate))")
    List<Order> findByCriteria(@Param("orderId") Long orderId,
                               @Param("customerName") String customerName,
                               @Param("phone") String phone,
                               @Param("status") OrderStatus status,
                               @Param("orderDate") LocalDate orderDate,
                               @Param("estimatedDeliveryDate") LocalDate estimatedDeliveryDate);
}
