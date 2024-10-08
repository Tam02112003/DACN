package DACN.DACN.services;


import DACN.DACN.entity.Product;
import DACN.DACN.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;



    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // Kiểm tra xem sản phẩm có tồn tại trước khi xóa
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Product not found");
        }
    }

    public Product updateProduct(@NotNull Product product) {
        Product existingSanpham = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingSanpham.setName(product.getName());
        existingSanpham.setPrice(product.getPrice());
        existingSanpham.setDescription(product.getDescription());
        existingSanpham.setImgUrl(product.getImgUrl());
        existingSanpham.setCategory(product.getCategory());
        existingSanpham.setUpdatedDate(new Date());
        return productRepository.save(existingSanpham);
    }
}
