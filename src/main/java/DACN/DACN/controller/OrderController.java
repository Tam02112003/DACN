package DACN.DACN.controller;

import DACN.DACN.entity.*;
import DACN.DACN.services.CartService;
import DACN.DACN.services.OrderService;
import DACN.DACN.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showCheckoutForm(Model model) {
        model.addAttribute("orders", new Order());
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice()); // Thêm tổng tiền vào model
        return "/cart/checkout"; // Chuyển đến trang checkout
    }

    @PostMapping
    public String processCheckout(@Valid @ModelAttribute("orders") Order order, BindingResult result, Model model, Principal principal) {
        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            // Nếu có lỗi, trả về lại trang checkout cùng với các thông báo lỗi
            model.addAttribute("cartItems", cartService.getCartItems());
            model.addAttribute("totalPrice", cartService.getTotalPrice()); // Tính tổng tiền
            return "/cart/checkout"; // Chuyển đến trang checkout
        }

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            model.addAttribute("errorMessage", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm trước khi thanh toán.");
            return "/cart/checkout";
        }

        // Thiết lập chi tiết đơn hàng và lưu vào cơ sở dữ liệu
        try {
            // Thiết lập trạng thái đơn hàng
            order.setStatus(OrderStatus.PENDING); // Thiết lập trạng thái ban đầu
            // Lấy thông tin người dùng đã đăng nhập
            Optional<User> optionalUser = userService.findByUsername(principal.getName()); // Lấy đối tượng User

            if (optionalUser.isPresent()) {
                User user = optionalUser.get(); // Lấy đối tượng User từ Optional
                order.setUser(user); // Gán người dùng vào đơn hàng
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin người dùng.");
                return "/cart/checkout"; // Trả về trang checkout nếu không tìm thấy người dùng
            }

            createAndSaveOrder(order, cartItems);//Tạo và lưu đơn hàng

            // Lưu lại danh sách sản phẩm vào Model trước khi xóa giỏ hàng
            model.addAttribute("cartItems", cartItems); // Sử dụng danh sách sản phẩm trước khi xóa
            model.addAttribute("order", order);

            // Xóa giỏ hàng sau khi đặt hàng thành công
            cartService.clearCart();
            model.addAttribute("message", "Đặt hàng thành công!");
            return "/cart/confirmation"; // Chuyển hướng đến trang cảm ơn
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra trong quá trình đặt hàng. Vui lòng thử lại.");
            return "/cart/checkout";
        }
    }
    // Phương thức phụ để chuyển đổi CartItem thành OrderDetail
    private OrderDetail convertToOrderDetail(CartItem item, Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(item.getProduct()); // Thiết lập sản phẩm
        orderDetail.setQuantity(item.getQuantity()); // Thiết lập số lượng
        orderDetail.setSize(item.getSize()); // Thiết lập kích thước
        orderDetail.setOrder(order); // Liên kết với đơn hàng
        // Tính và thiết lập tổng tiền cho từng chi tiết đơn hàng
        double totalPrice = item.getProduct().getPrice() * item.getQuantity();
        orderDetail.setTotalPrice(totalPrice); // Thiết lập tổng tiền
        return orderDetail;
    }
    // Phương thức riêng để tạo và lưu đơn hàng
    private void createAndSaveOrder(Order order, List<CartItem> cartItems) {

        // Thiết lập ngày giờ đặt hàng
        order.setOrderDate(new Date());

        // Tính toán và thiết lập ngày giao hàng dự kiến (sau 3 ngày)
        order.setEstimatedDeliveryDate(order.calculateEstimatedDeliveryDate());

        // Chuyển đổi CartItem thành OrderDetail và thiết lập liên kết với đơn hàng
        List<OrderDetail> orderDetails = cartItems.stream()
                .map(item -> convertToOrderDetail(item, order)) // Sử dụng phương thức riêng để tạo OrderDetail
                .collect(Collectors.toList());

        // Thiết lập chi tiết đơn hàng cho đơn hàng
        order.setOrderDetails(orderDetails);

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderService.createOrder(order);
    }

}
