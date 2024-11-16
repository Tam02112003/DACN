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

    public Page<Product> getProductsByCategoryId(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> getProductsByBrandId(Long brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByBrandId(brandId, pageable);
    }

    public Page<Product> getProductsByCategoryIdAndBrandId(Long categoryId, Long brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByCategoryIdAndBrandId(categoryId, brandId, pageable);
    }

    public Page<Product> getProductsByCategoryIdAndPriceRange(Long categoryId, String priceRange, int page, int size) {
        String[] prices = priceRange.split("-");
        Long minPrice = Long.parseLong(prices[0]);
        Long maxPrice = (prices.length > 1) ? Long.parseLong(prices[1]) : Long.MAX_VALUE;

        return productRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice, PageRequest.of(page - 1, size));
    }

    public Page<Product> getProductsByBrandIdAndPriceRange(Long brandId, String priceRange, int page, int size) {
        String[] prices = priceRange.split("-");
        Long minPrice = Long.parseLong(prices[0]);
        Long maxPrice = (prices.length > 1) ? Long.parseLong(prices[1]) : Long.MAX_VALUE;

        return productRepository.findByBrandIdAndPriceBetween(brandId, minPrice, maxPrice, PageRequest.of(page - 1, size));
    }

    public Page<Product> getProductsByPriceRange(String priceRange, int page, int size) {
        String[] prices = priceRange.split("-");
        Long minPrice = Long.parseLong(prices[0]);
        Long maxPrice = (prices.length > 1) ? Long.parseLong(prices[1]) : Long.MAX_VALUE;

        return productRepository.findByPriceBetween(minPrice, maxPrice, PageRequest.of(page - 1, size));
    }

    public Page<Product> getProductsByCategoryIdBrandIdAndPriceRange(Long categoryId, Long brandId, String priceRange, int page, int size) {
        String[] prices = priceRange.split("-");
        Long minPrice = Long.parseLong(prices[0]);
        Long maxPrice = (prices.length > 1) ? Long.parseLong(prices[1]) : Long.MAX_VALUE;

        return productRepository.findByCategoryIdAndBrandIdAndPriceBetween(categoryId, brandId, minPrice, maxPrice, PageRequest.of(page - 1, size));
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
        existingProduct.setBrand(product.getBrand());
        existingProduct.setContent(product.getContent());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setUpdatedDate(new Date());
        return productRepository.save(existingProduct);
    }

    public Page<Product> getProductsByStartingLetter(String letter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByNameStartingWithIgnoreCase(letter, pageable);
    }
}