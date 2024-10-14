package DACN.DACN.repository;

import DACN.DACN.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    // Phương thức tìm sản phẩm theo categoryId
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Phương thức tìm sản phẩm theo categoryId và tên chứa chuỗi tìm kiếm
    Page<Product> findByCategoryIdAndNameContaining(Long categoryId, String name, Pageable pageable);

    // Tổng giá của từng sản phẩm dựa theo tên (hoặc ID sản phẩm)
    @Query("SELECT p.name, SUM(p.price) AS total FROM Product p GROUP BY p.name")
    List<Object[]> findTotalByProductName();

    // Tổng giá của tất cả sản phẩm
    @Query("SELECT SUM(p.price) AS total FROM Product p")
    Double findTotalAllProducts();
}
