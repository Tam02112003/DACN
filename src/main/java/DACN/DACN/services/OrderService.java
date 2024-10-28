package DACN.DACN.services;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderDetail;
import DACN.DACN.entity.OrderStatus;
import DACN.DACN.entity.User;
import DACN.DACN.repository.OrderDetailRepository;
import DACN.DACN.repository.OrderRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Order createOrder(Order order) {
        // Lưu đối tượng Order vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(order); // Lưu đơn hàng
        // Lưu các chi tiết đơn hàng
        for (OrderDetail detail : savedOrder.getOrderDetails()) {
            detail.setOrder(savedOrder); // Thiết lập order cho chi tiết đơn hàng
            orderDetailRepository.save(detail); // Lưu chi tiết đơn hàng
        }

        return savedOrder; // Trả về đơn hàng đã lưu
    }
    /*public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }*/

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + orderId));
    }
    // Phương thức lấy đơn hàng theo người dùng
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByIdDesc(user);
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại")); // Ném lỗi nếu không tìm thấy

        // Cập nhật trạng thái cho đơn hàng
        order.setStatus(orderStatus);

        // Lưu đơn hàng đã cập nhật
        orderRepository.save(order);
    }
    public List<Order> findOrders(String orderId, String customerName, String phone, OrderStatus status, Date startDate, Date endDate, User user) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderId != null && !orderId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), Long.valueOf(orderId)));
            }

            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("customerName"), "%" + customerName + "%"));
            }

            if (phone != null && !phone.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + phone + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Kiểm tra và thêm tiêu chí tìm kiếm theo ngày đặt hàng
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("orderDate"), startDate, endDate));
            } else if (startDate != null) { // Chỉ có startDate
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            } else if (endDate != null) { // Chỉ có endDate
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }

            // Nếu user không phải là null, có thể thêm điều kiện cho user
            if (user != null) {
                predicates.add(criteriaBuilder.equal(root.get("user"), user));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }



    public List<Order> searchOrders(User user, String orderId, String status, Date startDate, Date endDate) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Thêm tiêu chí người dùng để chỉ lấy đơn hàng của người dùng đó
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            // Kiểm tra và thêm tiêu chí tìm kiếm theo ID đơn hàng
            if (orderId != null && !orderId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), Long.parseLong(orderId)));
            }

            // Kiểm tra và thêm tiêu chí tìm kiếm theo trạng thái đơn hàng
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(status)));
            }

            // Kiểm tra và thêm tiêu chí tìm kiếm theo ngày đặt hàng
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("orderDate"), startDate, endDate));
            } else if (startDate != null) { // Chỉ có startDate
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            } else if (endDate != null) { // Chỉ có endDate
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void save(Order order) {
        orderRepository.save(order); // Lưu đơn hàng vào cơ sở dữ liệu
    }

}
