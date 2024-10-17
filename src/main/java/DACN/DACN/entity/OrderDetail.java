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

    // Constructors
    public OrderDetail(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
