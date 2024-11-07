package DACN.DACN.services;

import DACN.DACN.entity.CartItem;
import DACN.DACN.entity.Order;
import DACN.DACN.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VnpayService {
    @Autowired
    private OrderService orderService;
    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String MERCHANT_ID = "SM1NR19S"; // mã định danh của bạn
    private static final String HASH_SECRET = "140HH44SAOTYLK5RWQYGRX80O2WI2BLQ"; // khóa bí mật

    public String createPaymentUrl(Order order, List<CartItem> cartItems) throws Exception {
        String transactionCode = "ORD" + System.currentTimeMillis(); // Mã đơn hàng duy nhất
        order.setTransactionCode(transactionCode);
        String amount = String.valueOf((int) (cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum() * 100)); // Tổng tiền tính bằng VND, nhân 100 vì VNPay yêu cầu đơn vị là đồng

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", MERCHANT_ID);
        vnpParams.put("vnp_Amount", amount);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transactionCode);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + transactionCode);
        vnpParams.put("vnp_OrderType", "billpayment");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", "http://localhost:8080/checkout/vnpay-return"); // URL để VNPay chuyển hướng sau khi thanh toán
        vnpParams.put("vnp_IpAddr", "127.0.0.1");
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Sắp xếp các tham số theo thứ tự alphabet
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && value.length() > 0) {
                // Build hashData
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                // Build query string
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                hashData.append('&');
                query.append('&');
            }
        }
        // Xóa ký tự '&' cuối cùng
        hashData.deleteCharAt(hashData.length() - 1);
        query.deleteCharAt(query.length() - 1);
        // Tạo mã bảo mật HMAC SHA512
        String vnpSecureHash = hmacSHA512(HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);
// Thiết lập ngày giờ đặt hàng
        order.setOrderDate(new Date());

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
        // Tạo URL thanh toán hoàn chỉnh
        return VNPAY_URL + "?" + query.toString();
    }

    // Phương thức tạo mã bảo mật HMAC SHA512
    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSHA512.init(secretKey);
        byte[] hmacData = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmacData);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
}