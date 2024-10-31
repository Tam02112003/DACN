package DACN.DACN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, unique = true)
    @NotBlank(message = "Tên người dùng là bắt buộc")
    @Size(min = 1, max = 50, message = "Tên người dùng phải từ 1-50 kí tự")
    private String username;

    @Column(name = "password", length = 250)
    @NotBlank(message = "Mật khẩu là bắt buộc")
    private String password;

    @Column(name = "email", length = 50, unique = true)
    @NotBlank(message = "Email là bắt buộc")
    @Email
    private String email;


    @Column(name = "fullname", length = 250)
    private String fullname;

    @Column(name = "phone", length = 15, unique = true)
    @Length(min = 10, max = 10, message = "Số điện thoại phải đúng 10 số")
    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại phải là số")
    private String phone;

    @Column(name = "address", length = 250)
    @Size(max = 400, message = "Địa chỉ của bạn quá dài!!! Vui lòng nhập 400 kí tự ")
    private String address;


    @Column(name = "provider", length = 50)
    private String provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;  // Danh sách đơn hàng của người dùng

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProductReview> reviews = new ArrayList<>();

    @Column(name = "profile_image_url") // Trường cho URL hình ảnh đại diện
    private String profileImageUrl; // Thuộc tính để lưu URL hình ảnh đại diện

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Product> viewedProducts = new ArrayList<>();  // Sản phẩm đã xem

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Product> purchasedProducts = new ArrayList<>();  // Sản phẩm đã mua



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> userRoles = this.getRoles();
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
