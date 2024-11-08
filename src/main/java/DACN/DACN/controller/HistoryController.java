package DACN.DACN.controller;

import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderStatus;
import DACN.DACN.entity.PaymentStatus;
import DACN.DACN.entity.User;
import DACN.DACN.services.CartService;
import DACN.DACN.services.OrderService;
import DACN.DACN.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class HistoryController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found");
        }
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", OrderStatus.values()); // Thêm danh sách trạng thái
        return "admins/order/list";
    }

    // Phương thức cập nhật trạng thái đơn hàng
    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status, Model model) {
        // Chuyển đổi từ String sang OrderStatus
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase()); // Chuyển đổi string thành enum
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Trạng thái không hợp lệ!"); // Thêm thông báo lỗi
            return "redirect:/orders/list"; // Chuyển hướng về danh sách đơn hàng nếu có lỗi
        }
        // Tìm đơn hàng theo ID
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng!"); // Thêm thông báo lỗi nếu đơn hàng không tồn tại
            return "redirect:/orders/list";
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus(orderStatus);

        // Kiểm tra nếu trạng thái là DELIVERED, cập nhật ngày giao hàng thực tế và trạng thái thanh toán
        if (orderStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.PAID);//
            order.updateActualDeliveryDate();  // Cập nhật ngày giao hàng thực tế
        }
        if (orderStatus == OrderStatus.CANCELED) {
            order.setPaymentStatus(PaymentStatus.FAILED);//
        }
        // Lưu đơn hàng sau khi cập nhật trạng thái và ngày giao hàng
        orderService.save(order);

        // Thêm thông báo thành công (nếu cần)
        model.addAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");

        // Chuyển hướng về danh sách đơn hàng
        return "redirect:/orders/list"; // Đảm bảo bạn có một URL để hiển thị danh sách đơn hàng
    }


    @GetMapping("/details/{id}")
    public String orderDetails(@PathVariable("id") Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "admins/order/details";
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model, Principal principal) {
        // Lấy thông tin người dùng đã đăng nhập
        Optional<User> optionalUser = userService.findByUsername(principal.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); // Lấy đối tượng User từ Optional
            List<Order> orders = orderService.getOrdersByUser(user); // Lấy danh sách đơn hàng của người dùng
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin người dùng.");
        }

        return "customers/my-orders"; // Trả về view hiển thị danh sách đơn hàng
    }
    @GetMapping("/search")
    public String searchOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            Model model, Principal principal) {

        Optional<User> optionalUser = userService.findByUsername(principal.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Gọi searchOrders với tiêu chí tìm kiếm và người dùng
            List<Order> orders = orderService.searchOrders(user, orderId, status, startDate, endDate);

            model.addAttribute("orders", orders);
            model.addAttribute("orderStatuses", OrderStatus.values());
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin người dùng.");
        }
        return "customers/my-orders";
    }
    @GetMapping("/find")
    public String listOrders(Model model, Principal principal,
                             @RequestParam(required = false) String orderId,
                             @RequestParam(required = false) String customerName,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) OrderStatus status,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        User user = userService.findByUsername(principal.getName()).orElse(null); // Lấy thông tin người dùng

        List<Order> orders = orderService.findOrders(orderId, customerName, phone, status, startDate, endDate, user);
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "admins/order/list";
    }
}
