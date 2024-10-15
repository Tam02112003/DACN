package DACN.DACN.controller;

import DACN.DACN.entity.CartItem;
import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderDetail;
import DACN.DACN.services.CartService;
import DACN.DACN.services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showCheckoutForm(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        return "/cart/checkout1"; // Chuyển đến trang checkout
    }
    @PostMapping
    public String processCheckout(String customerName, String phone, String address, String paymentMethod, Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        // Tạo đơn hàng mới
        Order order = new Order(customerName, phone, address, paymentMethod);
        // Thiết lập ngày giờ đặt hàng
        order.setOrderDate(new Date());
        // Chuyển đổi CartItem thành OrderDetail và thiết lập order cho mỗi OrderDetail
        List<OrderDetail> orderDetails = cartItems.stream()
                .map(item -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setProduct(item.getProduct()); // Thiết lập sản phẩm
                    orderDetail.setQuantity(item.getQuantity()); // Thiết lập số lượng
                    orderDetail.setOrder(order); // Thiết lập liên kết với đơn hàng
                    return orderDetail;
                })
                .collect(Collectors.toList());

        // Thiết lập chi tiết đơn hàng cho đơn hàng
        order.setOrderDetails(orderDetails);

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderService.createOrder(order);

        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartService.clearCart();
        model.addAttribute("message", "Đặt hàng thành công!");
        return "/cart/success"; // Chuyển hướng đến giỏ hàng hoặc trang cảm ơn
    }
}
