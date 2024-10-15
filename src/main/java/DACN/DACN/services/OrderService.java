package DACN.DACN.services;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderDetail;
import DACN.DACN.repository.OrderDetailRepository;
import DACN.DACN.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
