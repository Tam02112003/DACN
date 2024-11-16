package DACN.DACN.controller;

import DACN.DACN.entity.User;
import DACN.DACN.services.OrderService;
import DACN.DACN.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequiredArgsConstructor

public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private final UserService userService;

    @GetMapping("/admin")
    public String showDoashboard(Model model) {

        return "/admins/dashboard";
    }
    /*@GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Thêm dữ liệu doanh thu vào model
        model.addAttribute("todayRevenue", orderService.getTodayRevenue());
        model.addAttribute("weekRevenue", orderService.getThisWeekRevenue());
        model.addAttribute("monthRevenue", orderService.getThisMonthRevenue());
        model.addAttribute("yearRevenue", orderService.getThisYearRevenue());
        // Thống kê doanh thu theo tháng cho năm hiện tại
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Double[] monthlyRevenue = orderService.getMonthlyRevenue(currentYear);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        // Lấy doanh thu hàng ngày cho tháng hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Tháng tính từ 0
        Double[] dailyRevenue = orderService.getDailyRevenue(currentYear, currentMonth);
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);
        return "admins/statistics";
    }*/
    @GetMapping("/statistics")
    public String showStatistics(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model) {

        // Nếu không có, dùng tháng và năm hiện tại
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        year = (year == null) ? currentYear : year;
        month = (month == null) ? currentMonth : month;

        // Thêm dữ liệu doanh thu vào model
        model.addAttribute("todayRevenue", orderService.getTodayRevenue());
        model.addAttribute("weekRevenue", orderService.getThisWeekRevenue());
        model.addAttribute("monthRevenue", orderService.getThisMonthRevenue());
        model.addAttribute("yearRevenue", orderService.getThisYearRevenue());

        // Thống kê doanh thu theo tháng cho năm hiện tại
        Double[] monthlyRevenue = orderService.getMonthlyRevenue(year);
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        // Lấy doanh thu hàng ngày cho tháng và năm đã chọn
        Double[] dailyRevenue = orderService.getDailyRevenue(year, month);
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("currentMonth", month);
        model.addAttribute("currentYear", year);

        return "admins/statistics";
    }

    @GetMapping("/profile-admin")
    public String getCurrentUser(Model model) {
        String username = getCurrentUsername();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admins/profile";
        } else {
            model.addAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    @PostMapping("/profile-admin/upload-image")
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
            return "redirect:/profile-admin";
        } else {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tìm thấy.");
            return "error";
        }
    }

    @PostMapping("/profile-admin/update")
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
            return "redirect:/profile-admin";
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
