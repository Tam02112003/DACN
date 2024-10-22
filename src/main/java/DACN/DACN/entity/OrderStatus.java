package DACN.DACN.entity;

public enum OrderStatus {
    PENDING,  // Đang chờ xử lý
    PROCESSING,  // Đang xử lý
    SHIPPED,  // Đã giao hàng
    DELIVERED,  // Đã nhận hàng
    CANCELED  // Đã hủy
}
