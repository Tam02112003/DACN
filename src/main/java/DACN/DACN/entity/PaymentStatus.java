package DACN.DACN.entity;

public enum PaymentStatus {
    PENDING("Đang chờ thanh toán"),
    PAID("Đã thanh toán"),
    FAILED("Thanh toán thất bại");


    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
