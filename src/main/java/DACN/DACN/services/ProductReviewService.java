package DACN.DACN.services;

import DACN.DACN.entity.Product;
import DACN.DACN.entity.ProductReview;
import DACN.DACN.repository.ProductRepository;
import DACN.DACN.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductReview> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    public Map<Integer, Long> countReviewsByRatingForProduct(Long productId) {
        Map<Integer, Long> ratingCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = reviewRepository.countByProductIdAndRating(productId, i);
            ratingCounts.put(i, count);
        }
        return ratingCounts;
    }

    public void saveReview(ProductReview review) {
        reviewRepository.save(review); // Lưu bình luận
    }
}