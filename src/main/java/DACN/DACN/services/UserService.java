package DACN.DACN.services;

import DACN.DACN.Provider;
import DACN.DACN.Role;
import DACN.DACN.entity.User;
import DACN.DACN.repository.IRoleRepository;
import DACN.DACN.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/*import java.util.logging.Logger;*/


@Service
/*@Slf4j*/
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(Provider.LOCAL);
        userRepository.save(user);
        log.info("User {} saved successfully.", user.getUsername());
    }
    // Lưu thông tin người dùng sau khi cập nhật thông tin vào cơ sở dữ liệu .
    public void update(@NotNull User user) {
        userRepository.save(user);
    }
    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
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
        user.setProvider(Provider.GOOGLE);
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
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}