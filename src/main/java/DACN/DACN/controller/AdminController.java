package DACN.DACN.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor

public class AdminController {

    @GetMapping("/admin")
    public String showDoashboard(Model model) {

        return "/admins/dashboard";
    }
    @GetMapping("/admin1")
    public String showDoashboard2(Model model) {

        return "/admins/index";
    }
}
