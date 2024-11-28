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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @NotBlank(message = "Tên sản phẩm không được để trống") @Size(max = 150, min = 1, message = "Tên phải ít hơn 150 ký tự") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Tên sản phẩm không được để trống") @Size(max = 150, min = 1, message = "Tên phải ít hơn 150 ký tự") String name) {
        this.name = name;
    }

    public @Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự") String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public @NotNull(message = "Giá là bắt buộc") Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Giá là bắt buộc") Double price) {
        this.price = price;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}