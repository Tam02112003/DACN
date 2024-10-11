package DACN.DACN.controller;


import DACN.DACN.entity.Category;
import DACN.DACN.entity.Product;
import DACN.DACN.services.CategoryService;
import DACN.DACN.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller

    @RequiredArgsConstructor

    public class CustomerController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/home")
    public String showHome(Model model) {

        return "/customers/index";
    }
    @GetMapping("/contact")
    public String showContact(Model model) {

        return "/customers/contact";
    }
    @GetMapping("/blog")
    public String showBlog(Model model) {

        return "/customers/blog";
    }
        @GetMapping("/shop")
        public String showProductList(
                @RequestParam(defaultValue = "1") int page, // Trang mặc định là 1
                @RequestParam(defaultValue = "") String search, // Tìm kiếm
                @RequestParam(required = false) Long categoryId, // ID thể loại (có thể null)
                @RequestParam(defaultValue = "6") int size,
                Model model) {
            // Kiểm tra và đảm bảo page không nhỏ hơn 1
            if (page < 1) {
                page = 1;
            }
            // Gọi phương thức từ productService để lấy danh sách sản phẩm theo thể loại, phân trang và tìm kiếm
            Page<Product> productPage;
            if (categoryId != null) {
                productPage = productService.getProductsByCategoryId(categoryId, page, search, size);
            } else {
                productPage = productService.getProducts(page, search, size);
            }
            // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
            List<Category> categories = categoryService.getAllCategories();

            model.addAttribute("categories", categories);
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("search", search);

            return "customers/category";  // Đảm bảo rằng đường dẫn này là chính xác
        }
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "customers/single-product";
    }
}
