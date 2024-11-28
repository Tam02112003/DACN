package DACN.DACN.controller;
import DACN.DACN.entity.Category;
import DACN.DACN.entity.Product;
import DACN.DACN.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String listCategories(@RequestParam(defaultValue = "1") int page, // Trang mặc định là 1
                                 @RequestParam(defaultValue = "") String search, // Tìm kiếm
                                 Model model) {
        // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
        Page<Category> categoryPage = categoryService.getCategories(page, search);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("search", search);
        return "admins/category/list"; // Đường dẫn đến file template list.html
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "admins/category/create"; // Đường dẫn đến file template create.html
    }

    @PostMapping
    public String createCategory(@ModelAttribute @Valid Category category,BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Nếu có lỗi, trả về lại trang thêm thể loại và hiển thị thông báo
            return "admins/category/create"; // Tên view của form thêm thể loại
        }
        categoryService.createCategory(category); // Lưu thể loại mới
        return "redirect:/categories/list"; // Chuyển hướng về trang danh sách
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id); // Lấy thể loại theo ID
        if (category != null) {
            model.addAttribute("category", category); // Đưa thể loại vào model để hiển thị trong form
            return "/admins/category/edit";
        }
        return "redirect:/categories/list"; // Nếu không tìm thấy, chuyển hướng về danh sách
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, @Valid BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "admins/category/edit";
        }
        categoryService.updateCategory(id, category); // Cập nhật thông tin thể loại
        return "redirect:/categories/list"; // Chuyển hướng về danh sách thể loại
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories/list"; // Chuyển hướng về danh sách thể loại
    }
}
