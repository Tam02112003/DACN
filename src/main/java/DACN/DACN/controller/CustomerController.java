package DACN.DACN.controller;


import DACN.DACN.entity.*;
import DACN.DACN.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller

    @RequiredArgsConstructor

    public class CustomerController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private NewsService newsService;
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
    @Autowired
    private ProductRecommendationService recommendationService;

    @GetMapping("/home")
    public String showHome( @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "") String search,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(defaultValue = "6") int size,
                            Model model) {
        if (page < 1) {
            page = 1;
        }
        Page<Product> productPage;
        if (categoryId != null) {
            productPage = productService.getProductsByCategoryId(categoryId, page, size);
        } else {
            productPage = productService.getProducts(page, search, size);
        }
        // Lấy 9 sản phẩm ngẫu nhiên
        List<Product> randomProducts = recommendationService.getRandomProducts(9);
        List<Category> categories = categoryService.getAllCategories();


        model.addAttribute("categories", categories);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("randomProducts", randomProducts);
        return "/customers/index";
    }

    @GetMapping("/shop")
    public String showProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        // Đảm bảo page không nhỏ hơn 1
        if (page < 1) {
            page = 1;
        }
        Page<Product> productPage;
        // Kiểm tra và xử lý priceRange để tránh lỗi
        if (priceRange != null && priceRange.isEmpty()) {
            priceRange = null; // Gán về null nếu là chuỗi rỗng
        }
        // Lọc theo categoryId, brandId và priceRange
        if (categoryId != null && brandId != null && priceRange != null) {
            productPage = productService.getProductsByCategoryIdBrandIdAndPriceRange(categoryId, brandId, priceRange, page, size);
        }
        // Lọc theo categoryId và brandId mà không có priceRange
        else if (categoryId != null && brandId != null) {
            productPage = productService.getProductsByCategoryIdAndBrandId(categoryId, brandId, page, size);
        }
        // Lọc theo categoryId và priceRange
        else if (categoryId != null && priceRange != null) {
            productPage = productService.getProductsByCategoryIdAndPriceRange(categoryId, priceRange, page, size);
        }
        // Lọc theo brandId và priceRange
        else if (brandId != null && priceRange != null) {
            productPage = productService.getProductsByBrandIdAndPriceRange(brandId, priceRange, page, size);
        }
        // Lọc theo chỉ categoryId
        else if (categoryId != null) {
            productPage = productService.getProductsByCategoryId(categoryId, page, size);
        }
        // Lọc theo chỉ brandId
        else if (brandId != null) {
            productPage = productService.getProductsByBrandId(brandId, page, size);
        }
        // Lọc theo priceRange
        else if (priceRange != null) {
            productPage = productService.getProductsByPriceRange(priceRange, page, size);
        }
        // Nếu không có tham số nào
        else {
            productPage = productService.getProducts(page, search, size);
        }

        // Lấy danh sách sản phẩm ngẫu nhiên
        List<Product> randomProducts = recommendationService.getRandomProducts(9);
        List<Category> categories = categoryService.getAllCategories();
        List<Brand> brands = brandService.getAllBrands();

        // Gửi các thuộc tính vào model
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("randomProducts", randomProducts);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("priceRange", priceRange);

        return "customers/category";
    }

    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model,
                                   @AuthenticationPrincipal User user, HttpSession session) {
        Product product = productService.getProductById(id);
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại: " + id);
        }

        // Thêm sản phẩm vào danh sách đã xem của người dùng nếu đã đăng nhập
        if (user != null) {
            if (!user.getViewedProducts().contains(product)) {
                user.getViewedProducts().add(product);
                userService.update(user);  // Lưu thay đổi trong UserService
            }
        } else {
            // Nếu chưa đăng nhập, lưu sản phẩm vào danh sách đã xem trong session
            List<Product> viewedProducts = (List<Product>) session.getAttribute("viewedProducts");
            if (viewedProducts == null) {
                viewedProducts = new ArrayList<>();
            }
            if (!viewedProducts.contains(product)) {
                viewedProducts.add(product);
            }
            session.setAttribute("viewedProducts", viewedProducts);
        }

        // Lấy danh sách đánh giá và kích cỡ
        List<ProductReview> reviews = productReviewService.getReviewsByProductId(id);
        List<Size> sizes = sizeService.getSizesByCategory(product.getCategory().getId());

        // Đếm số lượng đánh giá theo từng mức sao
        Map<Integer, Long> ratingCounts = productReviewService.countReviewsByRatingForProduct(id);

        // Tính tổng số lượng đánh giá và trung bình số sao
        double totalRating = 0;
        long totalReviews = 0;
        for (Map.Entry<Integer, Long> entry : ratingCounts.entrySet()) {
            totalRating += entry.getKey() * entry.getValue();
            totalReviews += entry.getValue();
        }
        double averageRating = (totalReviews > 0) ? totalRating / totalReviews : 0;

        // Lấy các sản phẩm gợi ý dựa trên sản phẩm đã xem
        List<Product> recommendedProducts = recommendationService.getRecommendedProducts(user, session);

        // Thêm dữ liệu vào model
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sizes", sizes);
        model.addAttribute("reviews", reviews);
        model.addAttribute("ratingCounts", ratingCounts);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("review", new ProductReview());
        model.addAttribute("recommendedProducts", recommendedProducts); // Thêm gợi ý sản phẩm

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

    @GetMapping("/blog")
    public String showBlog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<News> newsPage = newsService.getAllNews(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Định dạng ngày cho mỗi bài viết
        for (News news : newsPage.getContent()) {
            if (news.getCreatedAt() != null) {
                news.setFormattedCreatedAt(news.getCreatedAt().format(formatter));
            }
        }

        model.addAttribute("newsPage", newsPage);
        return "/customers/blog";
    }
    @GetMapping("blog/detail/{id}")
    public String getBlogDetail(@PathVariable Long id, Model model) {
        News news = newsService.getNewsById(id);

        // Kiểm tra xem bài viết có tồn tại không
        if (news != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng ngày
            news.setFormattedCreatedAt(news.getCreatedAt().format(formatter)); // Định dạng thời gian
            // Lấy bài viết trước và tiếp theo
            News previousNews = newsService.getPreviousNews(id);
            News nextNews = newsService.getNextNews(id);

            model.addAttribute("previousNews", previousNews);
            model.addAttribute("nextNews", nextNews);
        }

        model.addAttribute("news", news);

        return "/customers/single-blog";
    }
}
