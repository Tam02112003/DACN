package DACN.DACN.repository;

import DACN.DACN.entity.Category;
import DACN.DACN.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm kiếm sản phẩm bắt đầu bằng chuỗi nhập vào (không phân biệt chữ hoa chữ thường)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT(?1, '%'))")
    Page<Product> findByNameStartingWithIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm với LIKE không phân biệt chữ hoa chữ thường
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Phương thức tìm kiếm sản phẩm theo brandId
    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    // Tìm kiếm sản phẩm theo brandId và tên chứa chuỗi tìm kiếm
    Page<Product> findByBrandIdAndNameContainingIgnoreCase(Long brandId, String name, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và brandId mà không có điều kiện về tên
    Page<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và brandId và tên chứa chuỗi tìm kiếm
    Page<Product> findByCategoryIdAndBrandIdAndNameContainingIgnoreCase(Long categoryId, Long brandId, String name, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Tìm kiếm sản phẩm theo danh sách các thể loại
    List<Product> findByCategoryIn(Set<Category> categories);

    // Tìm kiếm sản phẩm theo categoryId và tên chứa chuỗi tìm kiếm
    Page<Product> findByCategoryIdAndNameContaining(Long categoryId, String name, Pageable pageable);

    // Tổng giá của từng sản phẩm dựa theo tên (hoặc ID sản phẩm)
    @Query("SELECT p.name, SUM(p.price) AS total FROM Product p GROUP BY p.name")
    List<Object[]> findTotalByProductName();

    // Tổng giá của tất cả sản phẩm
    @Query("SELECT SUM(p.price) AS total FROM Product p")
    Double findTotalAllProducts();

    // Lấy danh sách sản phẩm ngẫu nhiên
    @Query("SELECT p FROM Product p ORDER BY RAND()")
    List<Product> findRandomProducts(Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và khoảng giá
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.price BETWEEN ?2 AND ?3")
    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo brandId và khoảng giá
    @Query("SELECT p FROM Product p WHERE p.brand.id = ?1 AND p.price BETWEEN ?2 AND ?3")
    Page<Product> findByBrandIdAndPriceBetween(Long brandId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId, brandId và khoảng giá
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.brand.id = ?2 AND p.price BETWEEN ?3 AND ?4")
    Page<Product> findByCategoryIdAndBrandIdAndPriceBetween(Long categoryId, Long brandId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo khoảng giá
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2")
    Page<Product> findByPriceBetween(Long minPrice, Long maxPrice, Pageable pageable);
}