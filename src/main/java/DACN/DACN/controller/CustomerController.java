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
                Model model) {
            // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
            Page<Category> categoryPage = categoryService.getCategories(page, search);
            // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
            Page<Product> productPage = productService.getProducts(page, search);
            model.addAttribute("categories", categoryPage.getContent());
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
