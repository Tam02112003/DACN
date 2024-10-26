package DACN.DACN.services;

import DACN.DACN.Provider;
import DACN.DACN.Role;
import DACN.DACN.entity.User;
import DACN.DACN.repository.IRoleRepository;
import DACN.DACN.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User {} saved successfully.", user.getUsername());
    }

    // Lưu hình ảnh của người dùng
    public void saveImage(User user, MultipartFile image) {
        String uploadDir = "D:/DACN/DACN/src/main/resources/static/uploads/profile-pictures";

        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = user.getUsername() + "_" + image.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, fileName);
            image.transferTo(filepath.toFile());

            user.setProfileImageUrl("uploads/profile-pictures/" + fileName);
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Error saving image for user {}: {}", user.getUsername(), e.getMessage());
        }
    }

    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
                    userRepository.save(user);
                    log.info("Default role set for user {}.", username);
                },
                () -> {
                    log.warn("User {} not found while setting default role.", username);
                    throw new UsernameNotFoundException("User not found");
                }
        );
    }

    // Tải thông tin chi tiết người dùng để xác thực.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Lưu người dùng từ OAuth.
    public void saveOauthUser(String email, @NotNull String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.info("User {} already exists, skipping save.", username);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(username));
        user.setProvider(Provider.GOOGLE.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        userRepository.save(user);
        log.info("OAuth user {} saved successfully.", username);
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User existingUser, User updatedUser) {
        existingUser.setFullname(updatedUser.getFullname());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        userRepository.save(existingUser);
        log.info("User {} updated successfully.", existingUser.getUsername());
    }
}