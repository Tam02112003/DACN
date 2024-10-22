package DACN.DACN.services;

import DACN.DACN.entity.ProductReview;
import DACN.DACN.repository.ProductRepository;
import DACN.DACN.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductReview> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public void addReview(ProductReview review) {
        reviewRepository.save(review);
    }
    public void saveReview(ProductReview review) {
        reviewRepository.save(review); // Lưu bình luận
    }
}