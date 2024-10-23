package DACN.DACN.controller;


import DACN.DACN.entity.*;
import DACN.DACN.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@Controller

    @RequiredArgsConstructor

    public class CustomerController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductReviewService productReviewService;
    @Autowired
    private SizeService sizeService;
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
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại: " + id);
        }

        List<ProductReview> reviews = productReviewService.getReviewsByProductId(id);
        List<Size> sizes = sizeService.getSizesByCategory(product.getCategory().getId());

        // Đếm số lượng đánh giá theo từng mức sao
        Map<Integer, Long> ratingCounts = productReviewService.countReviewsByRatingForProduct(id);

        // Tính tổng số lượng đánh giá và trung bình số sao
        double totalRating = 0;
        long totalReviews = 0;
        for (Map.Entry<Integer, Long> entry : ratingCounts.entrySet()) {
            totalRating += entry.getKey() * entry.getValue(); // tổng sao (mức sao * số lượng đánh giá)
            totalReviews += entry.getValue(); // tổng số lượng đánh giá
        }

        double averageRating = (totalReviews > 0) ? totalRating / totalReviews : 0;

        // Thêm dữ liệu vào model
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sizes", sizes);
        model.addAttribute("reviews", reviews);
        model.addAttribute("ratingCounts", ratingCounts);
        model.addAttribute("averageRating", averageRating); // Trung bình sao
        model.addAttribute("totalReviews", totalReviews);   // Tổng số lượng đánh giá
        model.addAttribute("review", new ProductReview());

        return "customers/single-product";
    }

    // Phương thức POST để thêm bình luận
    @PostMapping("/detail/{id}/review")
    public String addReview(@PathVariable Long id,
                            @ModelAttribute ProductReview review,
                            Principal principal,
                            RedirectAttributes redirectAttributes
                            ) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để bình luận!");
            return "redirect:/login"; // Chuyển hướng về trang đăng nhập
        }

        // Lấy tên người dùng từ Principal
        String username = principal.getName(); // Họ tên người dùng

        // Tìm người dùng trong cơ sở dữ liệu
        Optional<User> optionalUser = userService.findByUsername(username); // Sử dụng Optional
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại!");
            return "redirect:/login"; // Chuyển hướng về trang đăng nhập hoặc trang lỗi
        }

        User currentUser = optionalUser.get(); // Lấy đối tượng User từ Optional

        // Thiết lập sản phẩm và người dùng cho review
        review.setCreateDate(new Date()); // Gán giá trị thời gian hiện tại cho createDate
        Product product = productService.getProductById(id); // Lấy sản phẩm theo ID
        review.setProduct(product);
        review.setUser(currentUser); // Gán người dùng đã đăng nhập

        productReviewService.saveReview(review); // Lưu bình luận vào cơ sở dữ liệu

        redirectAttributes.addFlashAttribute("message", "Bình luận đã được thêm thành công!");
        return "redirect:/detail/" + id; // Chuyển hướng về trang chi tiết sản phẩm
    }
    @GetMapping("order/details/{id}")
    public String orderDetails(@PathVariable("id") Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "customers/order-detail";
    }

}
