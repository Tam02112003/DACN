package DACN.DACN.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "customer_name", nullable = false)
    private String customerName;  // Tên khách hàng

    @Column(name = "phone", nullable = false)
    private String phone;  // Số điện thoại

    @Column(name = "address", nullable = false)
    private String address;  // Địa chỉ

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;  // Phương thức thanh toán

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date", nullable = false)
    private Date orderDate;  // Ngày đặt hàng

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;  // Danh sách chi tiết đơn hàng


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
}
