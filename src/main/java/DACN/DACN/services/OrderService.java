package DACN.DACN.services;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderDetail;
import DACN.DACN.entity.OrderStatus;
import DACN.DACN.entity.User;
import DACN.DACN.repository.OrderDetailRepository;
import DACN.DACN.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + orderId));
    }
    // Phương thức lấy đơn hàng theo người dùng
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
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

}
