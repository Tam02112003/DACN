package DACN.DACN.repository;

import DACN.DACN.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    // Phương thức tìm sản phẩm theo categoryId
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Phương thức tìm sản phẩm theo categoryId và tên chứa chuỗi tìm kiếm
    Page<Product> findByCategoryIdAndNameContaining(Long categoryId, String name, Pageable pageable);
}
