package DACN.DACN.controller;

import DACN.DACN.entity.Category;
import DACN.DACN.entity.Size;
import DACN.DACN.services.CategoryService;
import DACN.DACN.services.SizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sizes")
public class SizeController {
    @Autowired
    private SizeService sizeService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public String listSizes(Model model) {
        List<Size> sizes = sizeService.getAllSizes();
        if (sizes == null) {
            sizes = new ArrayList<>(); // Tạo danh sách rỗng để tránh lỗi NullPointerException
        }
        model.addAttribute("sizes", sizes);
        return "admins/size/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("size", new Size());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/size/create";
    }

    @PostMapping("/create")
    public String createSize(@Valid @ModelAttribute Size size, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Nếu có lỗi, trả về lại trang thêm size và hiển thị thông báo
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "admins/size/create"; // Tên view của form size
        }
        sizeService.saveSize(size);
        return "redirect:/sizes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Size size = sizeService.getSizeById(id);
        model.addAttribute("size", size);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/size/edit";
    }

    @PostMapping("/update/{id}")
    public String editSize(@ModelAttribute Size size, BindingResult bindingResult, Model model) {
        sizeService.saveSize(size);
        return "redirect:/sizes";
    }

    @GetMapping("/delete/{id}")
    public String deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return "redirect:/sizes";
    }
}
