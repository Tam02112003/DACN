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
}