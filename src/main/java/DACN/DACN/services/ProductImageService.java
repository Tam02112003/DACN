package DACN.DACN.services;

import DACN.DACN.entity.ProductImage;
import DACN.DACN.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public void deleteAllByProductId(Long productId) {
        productImageRepository.deleteAllByProduct_Id(productId);
    }

    public List<ProductImage> getAllProductImages() {
        return productImageRepository.findAll();
    }

    public void addProductImage(ProductImage productImage) {
        productImageRepository.save(productImage);
    }
}
