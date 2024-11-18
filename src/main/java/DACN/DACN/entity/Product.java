package DACN.DACN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Người dùng mua sản phẩm

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, min = 1, message = "Tên phải ít hơn 150 ký tự")
    private String name;

    @Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Giá là bắt buộc")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand; // Tham chiếu đến thương hiệu

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> reviews = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    private String imgUrl;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = new Date();
    }
}