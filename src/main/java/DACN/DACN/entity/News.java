package DACN.DACN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Vui lòng nhập tên tác giả")
    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "description",columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Nội dung không được để trống")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imgUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Transient
    private String formattedCreatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Tiêu đề không được để trống") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Tiêu đề không được để trống") String title) {
        this.title = title;
    }

    public @NotBlank(message = "Vui lòng nhập tên tác giả") String getAuthor() {
        return author;
    }

    public void setAuthor(@NotBlank(message = "Vui lòng nhập tên tác giả") String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotBlank(message = "Nội dung không được để trống") String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "Nội dung không được để trống") String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
}