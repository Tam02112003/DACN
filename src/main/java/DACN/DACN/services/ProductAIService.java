package DACN.DACN.services;

import DACN.DACN.entity.Product;
import DACN.DACN.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAIService {
    private final ProductRepository productRepository;

    public ProductAIService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }


}
