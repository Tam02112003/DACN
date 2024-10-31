package DACN.DACN.services;

import DACN.DACN.entity.Product;
import DACN.DACN.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;



    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> getProducts(int page, String search, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    public Page<Product> getProductsByCategoryId(Long categoryId, int page, String search, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByCategoryIdAndNameContaining(categoryId, search, pageable);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Product not found");
        }
    }

    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImgUrl(product.getImgUrl());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setUpdatedDate(new Date());
        return productRepository.save(existingProduct);
    }
}
