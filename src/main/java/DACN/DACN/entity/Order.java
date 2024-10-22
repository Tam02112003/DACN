package DACN.DACN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")  // Tên bảng trong cơ sở dữ liệu
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID của đơn hàng

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 150, min = 1, message = "Tên phải ít hơn 150 ký tự")
    @Column(name = "customer_name", nullable = false)
    private String customerName;  // Tên khách hàng

    @NotBlank(message = "Số điện thoại không được để trống")
    @Length(min = 10, max = 10, message = "Số điện thoại phải đúng 10 số")
    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại phải là số")
    @Column(name = "phone", nullable = false)
    private String phone;  // Số điện thoại

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Column(name = "address", nullable = false)
    private String address;  // Địa chỉ

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;  // Phương thức thanh toán

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date", nullable = false)
    private Date orderDate;  // Ngày đặt hàng

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;  // Danh sách chi tiết đơn hàng

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING; // Mặc định là PENDING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Người dùng đặt đơn hàng

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Order(String customerName, String phone, String address, String paymentMethod) {
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }
    // Thêm phương thức tiện ích để thêm chi tiết đơn hàng
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }
    public void removeOrderDetail(OrderDetail orderDetail) {
        orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
    }

    // Phương thức tính tổng tiền của đơn hàng
    public double getTotalAmount() {
        return orderDetails.stream()
                .mapToDouble(OrderDetail::getTotalPrice) // Lấy totalPrice của từng OrderDetail
                .sum(); // Cộng dồn để ra tổng tiền
    }
}
