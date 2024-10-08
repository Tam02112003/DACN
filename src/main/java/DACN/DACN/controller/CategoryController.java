package DACN.DACN.controller;
import DACN.DACN.entity.Category;
import DACN.DACN.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/list")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admins/category/list"; // Đường dẫn đến file template list.html
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "/admins/category/create"; // Đường dẫn đến file template create.html
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category); // Lưu thể loại mới
        return "redirect:/categories/list"; // Chuyển hướng về trang danh sách
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id); // Lấy thể loại theo ID
        if (category != null) {
            model.addAttribute("category", category); // Đưa thể loại vào model để hiển thị trong form
            return "/admins/category/edit"; // Trả về view form.html
        }
        return "redirect:/categories/list"; // Nếu không tìm thấy, chuyển hướng về danh sách
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category categoryDetails) {
        categoryService.updateCategory(id, categoryDetails); // Cập nhật thông tin thể loại
        return "redirect:/categories/list"; // Chuyển hướng về danh sách thể loại
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories/list"; // Chuyển hướng về danh sách thể loại
    }
}
