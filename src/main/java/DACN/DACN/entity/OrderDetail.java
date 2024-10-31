package DACN.DACN.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_details")  // Tên bảng trong cơ sở dữ liệu
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID của chi tiết đơn hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  // Đơn hàng tương ứng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Sản phẩm

    @Column(name = "quantity", nullable = false)
    private int quantity;  // Số lượng sản phẩm

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    // Constructors
    public OrderDetail(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;  // Tính tổng tiền
    }
    // Phương thức để cập nhật lại tổng tiền khi số lượng hoặc giá thay đổi
    public void updateTotalPrice() {
        this.totalPrice = this.product.getPrice() * this.quantity;
    }
}