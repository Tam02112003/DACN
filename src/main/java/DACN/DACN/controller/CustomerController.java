package DACN.DACN.controller;


import DACN.DACN.entity.Product;
import DACN.DACN.services.CategoryService;
import DACN.DACN.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public String showShop(Model model) {

        return "/customers/category";
    }
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "customers/single-product";
    }
}
