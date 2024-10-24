package DACN.DACN.entity;

public enum OrderStatus {
    PENDING("Đang chờ", "yellow"),  // Màu vàng
    PROCESSING("Đang xử lý", "orange"),  // Màu cam
    SHIPPED("Đang giao hàng", "blue"),  // Màu xanh dương
    DELIVERED("Đã nhận hàng", "green"),  // Màu xanh lá
    CANCELED("Đã hủy", "red");  // Màu đỏ

    private final String description;
    private final String color;

    OrderStatus(String description, String color) {
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public String getStatusInVietnamese() {
        return description; // Trả về mô tả bằng tiếng Việt
    }

}
