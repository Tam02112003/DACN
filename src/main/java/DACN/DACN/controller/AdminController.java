package DACN.DACN.controller;

import DACN.DACN.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor

public class AdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin")
    public String showDoashboard(Model model) {

        return "/admins/dashboard";
    }
    @GetMapping("/admin1")
    public String showDoashboard2(Model model) {

        return "/admins/index";
    }
    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Thêm dữ liệu doanh thu vào model
        model.addAttribute("todayRevenue", orderService.getTodayRevenue());
        model.addAttribute("weekRevenue", orderService.getThisWeekRevenue());
        model.addAttribute("monthRevenue", orderService.getThisMonthRevenue());
        model.addAttribute("yearRevenue", orderService.getThisYearRevenue());

        return "admins/statistics"; // Tên view template (HTML file)
    }
}
