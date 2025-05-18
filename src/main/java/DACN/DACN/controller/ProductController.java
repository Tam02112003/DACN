package DACN.DACN.controller;

import DACN.DACN.entity.Product;

import DACN.DACN.entity.ProductImage;
import DACN.DACN.entity.ProductReview;
import DACN.DACN.entity.User;
import DACN.DACN.services.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductReviewService productReviewService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String showProductList(
            @RequestParam(defaultValue = "1") int page, // Trang mặc định là 1
            @RequestParam(defaultValue = "") String search, // Tìm kiếm
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        // Kiểm tra và đảm bảo page không nhỏ hơn 1
        if (page < 1) {
            page = 1;
        }
        // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
        Page<Product> productPage = productService.getProductsNotDeleted(page, search, size);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);

        return "admins/product/list";
    }

    @GetMapping("/listdelete")
    public String showProductListDelete(
            @RequestParam(defaultValue = "1") int page, // Trang mặc định là 1
            @RequestParam(defaultValue = "") String search, // Tìm kiếm
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        // Kiểm tra và đảm bảo page không nhỏ hơn 1
        if (page < 1) {
            page = 1;
        }

        // Gọi phương thức từ productService để lấy danh sách sản phẩm đã xóa với phân trang và tìm kiếm
        Page<Product> productPage = productService.getProductsDeleted(page, search, size);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);

        return "admins/product/delete"; // Chuyển hướng tới trang hiển thị danh sách sản phẩm đã xóa
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/product/create";  // Đảm bảo rằng đường dẫn này là chính xác
    }


    //------------------------------------------------------------------------------
    @PostMapping("/create")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile imageFile, @RequestParam("productimages") MultipartFile[] imageList, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admins/product/create";  // Đảm bảo rằng đường dẫn này là chính xác
        }

        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImage(imageFile);
                product.setImgUrl("/img/" + imageName);  // Cập nhật đường dẫn hình ảnh
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(product);
        for (MultipartFile image : imageList) {
            if (!image.isEmpty()) {
                try {
                    String imageUrl = saveImage(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setImagePath("/img/" +imageUrl);
                    productImage.setProduct(product);
                    product.getImages().add(productImage);
                    productImageService.addProductImage(productImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/products";  // Chuyển hướng lại danh sách sản phẩm
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path dirImages = Paths.get("target/classes/static/img");
        if (!Files.exists(dirImages)) {
            Files.createDirectories(dirImages);
        }

        String newFileName = image.getOriginalFilename();

        Path pathFileUpload = dirImages.resolve(newFileName);
        Files.copy(image.getInputStream(), pathFileUpload,
                StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/product/edit";
    }
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result,@RequestParam("image") MultipartFile imageFile,
                                @RequestParam("productimages") MultipartFile[] imageList){
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            return "admins/products/edit/{id}";
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

        // Nếu không có hình ảnh phụ mới -> giữ nguyên các hình ảnh phụ cũ
        if (imageList.length == 0 || (imageList.length == 1 && imageList[0].isEmpty())) {
            Product existingProduct = productService.getProductById(id);
            product.setImages(existingProduct.getImages());
        } else {
            // Nếu có hình ảnh phụ mới -> xóa hình ảnh cũ và thêm hình ảnh mới
            productImageService.deleteAllByProductId(id); // Xóa tất cả hình ảnh phụ cũ
            for (MultipartFile image : imageList) {
                if (!image.isEmpty()) {
                    try {
                        String imageUrl = saveImage(image);
                        ProductImage productImage = new ProductImage();
                        productImage.setImagePath("/img/" + imageUrl);
                        productImage.setProduct(product);
                        product.getImages().add(productImage);
                        productImageService.addProductImage(productImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.softDelete(id);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm được xóa thành công!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi không xóa được sản phẩm!");
        }
        return "redirect:/products"; // Chuyển hướng về danh sách sản phẩm
    }
    @GetMapping("/restore/{id}")
    public String restoreProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.restore(id); // Gọi phương thức khôi phục sản phẩm
            redirectAttributes.addFlashAttribute("message", "Sản phẩm được khôi phục thành công!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi không khôi phục được sản phẩm!");
        }
        return "redirect:/products/listdelete"; // Chuyển hướng về danh sách sản phẩm đã xóa
    }
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/product/detail";
    }



}