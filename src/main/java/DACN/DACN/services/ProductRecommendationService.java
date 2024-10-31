package DACN.DACN.services;


import DACN.DACN.entity.Category;
import DACN.DACN.entity.Product;
import DACN.DACN.entity.User;
import DACN.DACN.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final ProductRepository productRepository;



    public List<Product> getRecommendedProducts(User user, HttpSession session) {
        Set<Category> categories = new HashSet<>();
        Set<Product> viewedProducts = new HashSet<>();
        Set<Product> purchasedProducts = new HashSet<>();

        if (user != null) {
            // Người dùng đã đăng nhập: lấy danh mục từ các sản phẩm đã xem hoặc mua
            categories = user.getViewedProducts().stream()
                    .map(Product::getCategory)
                    .collect(Collectors.toSet());
            viewedProducts = new HashSet<>(user.getViewedProducts());
            purchasedProducts = new HashSet<>(user.getPurchasedProducts());
        } else {
            // Người dùng chưa đăng nhập: lấy lịch sử xem từ session
            List<Product> sessionViewedProducts = (List<Product>) session.getAttribute("viewedProducts");
            if (sessionViewedProducts != null) {
                categories = sessionViewedProducts.stream()
                        .map(Product::getCategory)
                        .collect(Collectors.toSet());
                viewedProducts = new HashSet<>(sessionViewedProducts);
            }
        }

        // Lấy sản phẩm trong các danh mục tương ứng
        List<Product> recommendedProducts = productRepository.findByCategoryIn(categories);

        // Loại bỏ các sản phẩm đã xem hoặc mua
        recommendedProducts.removeAll(viewedProducts);
        recommendedProducts.removeAll(purchasedProducts);

        return recommendedProducts;
    }

    public List<Product> getRandomProducts(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return productRepository.findRandomProducts(pageable);
    }
}


