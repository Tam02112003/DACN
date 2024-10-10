package DACN.DACN.controller;

import DACN.DACN.entity.Product;

import DACN.DACN.entity.ProductImage;
import DACN.DACN.services.CategoryService;
import DACN.DACN.services.ProductService;
import DACN.DACN.services.ProductImageService;
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
import java.nio.file.StandardCopyOption;
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
    @Autowired
    private ProductImageService productImageService;


    ///////////////////////////////////
    /*@GetMapping("")
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/admins/product/list";  // Đảm bảo rằng đường dẫn này là chính xác
    }*/
    //////////////////////////////////


    @GetMapping("")
    public String showProductList(
            @RequestParam(defaultValue = "1") int page, // Trang mặc định là 1
            @RequestParam(defaultValue = "") String search, // Tìm kiếm
            Model model) {

        // Gọi phương thức từ productService để lấy danh sách sản phẩm với phân trang và tìm kiếm
        Page<Product> productPage = productService.getProducts(page, search);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);

        return "/admins/product/list";  // Đảm bảo rằng đường dẫn này là chính xác
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admins/product/create";  // Đảm bảo rằng đường dẫn này là chính xác
    }


    //------------------------------------------------------------------------------
    @PostMapping("/create")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile imageFile, @RequestParam("productimages") MultipartFile[] imageList) {
        if (result.hasErrors()) {
            return "/admins/product/create";  // Đảm bảo rằng đường dẫn này là chính xác
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
        // Đảm bảo rằng thư mục lưu trữ hình ảnh nằm trong thư mục static/img
        /*File saveFile = new ClassPathResource("static/img").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;*/
        Path dirImages = Paths.get("target/classes/static/img");
        if (!Files.exists(dirImages)) {
            Files.createDirectories(dirImages);
        }

        String newFileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());

        Path pathFileUpload = dirImages.resolve(newFileName);
        Files.copy(image.getInputStream(), pathFileUpload,
                StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/admins/product/edit";
    }
    @PostMapping("/edit/{id}")
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
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        return "redirect:/products"; // Chuyển hướng về danh sách sản phẩm
    }
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admins/product/detail";
    }
}
