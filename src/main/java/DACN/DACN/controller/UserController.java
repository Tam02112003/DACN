package DACN.DACN.controller;

import DACN.DACN.entity.User;
import DACN.DACN.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        // Kiểm tra nếu username đã tồn tại
        if (userService.isUsernameExist(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Tên người dùng đã tồn tại");
        }
        // Kiểm tra nếu email đã tồn tại
        if (userService.isEmailExist(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email đã được sử dụng");
        }
        // Nếu có lỗi, thêm lỗi vào model và trả về trang đăng ký
        if (bindingResult.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            return "users/register"; // Trả về trang đăng ký với lỗi
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String getCurrentUser(Model model) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "users/profile";
        } else {
            model.addAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    @PostMapping("/profile/upload-image")
    public String uploadImage(@RequestParam("image") MultipartFile image, Model model, RedirectAttributes redirectAttributes) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!image.isEmpty()) {
                try {
                    String imageName = saveImage(image);
                    user.setProfileImageUrl("/img/" + imageName);  // Cập nhật đường dẫn hình ảnh
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                userService.update(user);
                redirectAttributes.addFlashAttribute("message", "Hình ảnh đã được cập nhật thành công.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tải lên hình ảnh: " + e.getMessage());
            }
            return "redirect:/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    @PostMapping("/profile/update")
    public String updateUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            existingUser.setFullname(user.getFullname());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            // Giữ nguyên mật khẩu hiện tại
            /*existingUser.setPassword(existingUser.getPassword());*/

            userService.update(existingUser);
            redirectAttributes.addFlashAttribute("message", "Thông tin đã được cập nhật thành công.");
            return "redirect:/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path dirImages = Paths.get("target/classes/static/img");
        if (!Files.exists(dirImages)) {
            Files.createDirectories(dirImages);
        }

        String newFileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());

        Path pathFileUpload = dirImages.resolve(newFileName);
        Files.copy(image.getInputStream(), pathFileUpload,
                StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }
}