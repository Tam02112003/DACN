package DACN.DACN.controller;

import DACN.DACN.entity.User;
import DACN.DACN.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
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
    public String uploadImage(@RequestParam("image") MultipartFile image, Model model) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (image.isEmpty()) {
                model.addAttribute("error", "File không được để trống.");
                return "redirect:/profile";
            }

            try {
                userService.saveImage(user, image);
                model.addAttribute("message", "Hình ảnh đã được cập nhật thành công.");
            } catch (Exception e) {
                model.addAttribute("error", "Lỗi khi tải lên hình ảnh: " + e.getMessage());
            }

            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    @PostMapping("/profile/update")
    public String updateUser(@ModelAttribute("user") User user, Model model) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setFullname(user.getFullname());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());

            userService.save(existingUser);
            model.addAttribute("message", "Thông tin đã được cập nhật thành công.");
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}