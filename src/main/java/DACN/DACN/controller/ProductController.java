package DACN.DACN.controller;

import DACN.DACN.entity.Product;
import DACN.DACN.services.CategoryService;
import DACN.DACN.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/admins/product/list";
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admins/product/create";
    }

    @PostMapping("/create")
    public String  addProduct(@Valid Product sanPham, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/admins/product/create";
        }
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                sanPham.setImgUrl("/img/" +imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(sanPham);
        return "redirect:/products";
    }
    private String saveImage(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/img").getFile();
        String fileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admins/product/edit";
    }
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result,@RequestParam("image") MultipartFile imageFile ) {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            return "admins/products/edit";
        }
        // Nếu người dùng tải lên hình ảnh mới
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                product.setImgUrl("/img/" + imageName); // Cập nhật hình ảnh mới
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Nếu không upload hình ảnh mới, lấy hình ảnh cũ từ cơ sở dữ liệu
            Product existingProduct = productService.getProductById(id);
            product.setImgUrl(existingProduct.getImgUrl());
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        return "redirect:/products"; // Chuyển hướng về danh sách sản phẩm
    }
}
