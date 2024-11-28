package DACN.DACN.entity;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "review")
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Đảm bảo rằng người dùng đã đăng nhập

    @Column(columnDefinition = "TEXT")
    private String comment; // Bình luận

    @NotNull
    @Min(1) // Đảm bảo rating không dưới 1
    @Max(5) // Đảm bảo rating không vượt quá 5
    private int rating;

    @Temporal(TemporalType.TIMESTAMP) // Sử dụng để xác định kiểu dữ liệu là TIMESTAMP
    @Column(name = "create_date", nullable = false)
    private Date createDate;  // Ngày đặt bình luận

    // Constructor cho việc tạo mới bình luận
    public ProductReview(Product product, User user, String comment, int rating) {
        this.product = product;
        this.user = user;
        this.comment = comment;
        this.rating = rating;

    }

    // Constructor không tham số (JPA yêu cầu)
    public ProductReview() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Min(1)
    @Max(5)
    public int getRating() {
        return rating;
    }

    public void setRating(@Min(1) @Max(5) int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}