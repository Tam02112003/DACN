package DACN.DACN.controller;

import DACN.DACN.entity.*;
import DACN.DACN.repository.OrderRepository;
import DACN.DACN.services.CartService;
import DACN.DACN.services.OrderService;
import DACN.DACN.services.UserService;
import DACN.DACN.services.VnpayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private VnpayService vnpayService;
    @GetMapping
    public String showCheckoutForm(Model model) {
        model.addAttribute("orders", new Order());
        model.addAttribute("paymentMethods", PaymentMethod.values());
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
            model.addAttribute("paymentMethods", PaymentMethod.values());
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
            order.setPaymentStatus(PaymentStatus.PENDING);

            // Lấy thông tin người dùng đã đăng nhập
            Optional<User> optionalUser = userService.findByUsername(principal.getName()); // Lấy đối tượng User
            if (optionalUser.isPresent()) {
                User user = optionalUser.get(); // Lấy đối tượng User từ Optional
                order.setUser(user); // Gán người dùng vào đơn hàng
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin người dùng.");
                return "/cart/checkout"; // Trả về trang checkout nếu không tìm thấy người dùng
            }
            Date currentDate = new Date();
            order.setOrderDate(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            order.setEstimatedDeliveryDate(calendar.getTime());
            // Nếu phương thức thanh toán là VNPay, thực hiện xử lý thanh toán qua VNPay
            if (order.getPaymentMethod() == PaymentMethod.VNPAY) {
                try {
                    String paymentUrl = vnpayService.createPaymentUrl(order, cartItems); // Gọi service tạo URL thanh toán
                    return "redirect:" + paymentUrl; // Chuyển hướng đến VNPay để thanh toán
                } catch (Exception e) {
                    model.addAttribute("cartItems", cartService.getCartItems());
                    model.addAttribute("paymentMethods", PaymentMethod.values());
                    model.addAttribute("totalPrice", cartService.getTotalPrice()); // Tính tổng tiền
                    model.addAttribute("errorMessage", "Lỗi từ VNPay: " + e.getMessage());
                    return "/cart/checkout"; // Quay về trang checkout
                }
            }

            // Nếu là COD, tiếp tục xử lý như bình thường
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
    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String transactionCode  = params.get("vnp_TxnRef");
        System.out.println("vnp_ResponseCode: " + vnp_ResponseCode);
        try {
            // Tìm đơn hàng bằng transactionCode
            Order order = orderService.findByTransactionCode(transactionCode);

            if (order == null) {
                model.addAttribute("errorMessage", "Đơn hàng không tìm thấy.");
                return "/cart/checkout";
            }

            if ("00".equals(vnp_ResponseCode)) {
                model.addAttribute("message", "Thanh toán thành công.");
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setStatus(OrderStatus.PENDING);

            } else {
                model.addAttribute("errorMessage", "Thanh toán không thành công.");
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.CANCELED);

            }
            orderService.save(order);
            // Thêm dữ liệu giỏ hàng và đơn hàng để hiển thị trong trang xác nhận
            List<CartItem> cartItems = cartService.getCartItems(); // lấy thông tin các sản phẩm trong đơn hàng nếu lưu lại được
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("order", order);
            // Xóa giỏ hàng sau khi đặt hàng thành công
            cartService.clearCart();
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Mã đơn hàng không hợp lệ.");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            e.printStackTrace(); // In lỗi chi tiết để kiểm tra
        }

        return "/cart/confirmation";
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
        String transactionCode = "ORD" + System.currentTimeMillis();
        order.setTransactionCode(transactionCode);
        // Tính toán và thiết lập ngày giao hàng dự kiến (sau 3 ngày)
        order.setEstimatedDeliveryDate(order.calculateEstimatedDeliveryDate());
        // Kiểm tra và khởi tạo orderDetails nếu chưa được khởi tạo
        if (order.getOrderDetails() == null) {
            order.setOrderDetails(new ArrayList<>());
        }
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