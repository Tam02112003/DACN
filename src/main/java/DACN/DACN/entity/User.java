package DACN.DACN.entity;

import DACN.DACN.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@ToString
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

    @Column(name = "phone", length = 15, nullable = true )
    /*@Length(min = 10, max = 10, message = "Số điện thoại phải đúng 10 số")*/
    @Pattern(regexp = "^[0-9]{10}$|^$", message = "Số điện thoại phải là số và đúng 10 số (hoặc để trống)")
    private String phone;

    @Column(name = "address", length = 250)
    @Size(max = 400, message = "Địa chỉ của bạn quá dài!!! Vui lòng nhập 400 kí tự ")
    private String address;


    @Column(name = "provider", length = 50)
    @Enumerated(EnumType.STRING)
    private Provider provider;

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
    // Constructor với ID
    public User(Long id) {
        this.id = id;
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(@NotBlank(message = "Tên người dùng là bắt buộc") @Size(min = 1, max = 50, message = "Tên người dùng phải từ 1-50 kí tự") String username) {
        this.username = username;
    }

    public void setPassword(@NotBlank(message = "Mật khẩu là bắt buộc") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Email là bắt buộc") @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email là bắt buộc") @Email String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public @Size(max = 400, message = "Địa chỉ của bạn quá dài!!! Vui lòng nhập 400 kí tự ") String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 400, message = "Địa chỉ của bạn quá dài!!! Vui lòng nhập 400 kí tự ") String address) {
        this.address = address;
    }

    public @Pattern(regexp = "^[0-9]{10}$|^$", message = "Số điện thoại phải là số và đúng 10 số (hoặc để trống)") String getPhone() {
        return phone;
    }

    public void setPhone(@Pattern(regexp = "^[0-9]{10}$|^$", message = "Số điện thoại phải là số và đúng 10 số (hoặc để trống)") String phone) {
        this.phone = phone;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<Product> getViewedProducts() {
        return viewedProducts;
    }

    public void setViewedProducts(List<Product> viewedProducts) {
        this.viewedProducts = viewedProducts;
    }

    public List<Product> getPurchasedProducts() {
        return purchasedProducts;
    }

    public void setPurchasedProducts(List<Product> purchasedProducts) {
        this.purchasedProducts = purchasedProducts;
    }
    public User(){}
}
