package DACN.DACN.repository;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderStatus; // Nhớ import OrderStatus nếu bạn muốn tìm theo trạng thái
import DACN.DACN.entity.PaymentStatus;
import DACN.DACN.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserOrderByIdDesc(User user);  // Tìm đơn hàng theo người dùng
    List<Order> findAllByOrderByIdDesc();
    Order findByTransactionCode(String transactionCode);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.paymentStatus = 'PAID'")
    Double sumTotalAmountBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByDate(Date date);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE FUNCTION('WEEK', o.orderDate) = FUNCTION('WEEK', CURRENT_DATE) AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByWeek();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = FUNCTION('MONTH', CURRENT_DATE) AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByMonth();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) AND o.paymentStatus = 'PAID'")
    Double calculateRevenueByYear();

}
