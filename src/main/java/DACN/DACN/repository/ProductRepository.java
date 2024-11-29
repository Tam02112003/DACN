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

    List<Product> findAllByDeletedFalse(); // Lấy tất cả sản phẩm chưa bị xóa

    Page<Product> findAllByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findAllByDeletedTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm sản phẩm đã xóa theo tên (không phân biệt chữ hoa chữ thường)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND p.deleted = true")
    Page<Product> findDeletedProductsByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm sản phẩm bắt đầu bằng chuỗi nhập vào (không phân biệt chữ hoa chữ thường) và chưa bị xóa
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT(?1, '%')) AND p.deleted = false")
    Page<Product> findByNameStartingWithIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm với LIKE không phân biệt chữ hoa chữ thường và chưa bị xóa
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND p.deleted = false")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Phương thức tìm kiếm sản phẩm theo brandId và chưa bị xóa
    Page<Product> findByBrandIdAndDeletedFalse(Long brandId, Pageable pageable);

    // Tìm kiếm sản phẩm theo brandId và tên chứa chuỗi tìm kiếm mà chưa bị xóa
    Page<Product> findByBrandIdAndNameContainingIgnoreCaseAndDeletedFalse(Long brandId, String name, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và brandId mà không có điều kiện về tên và chưa bị xóa
    Page<Product> findByCategoryIdAndBrandIdAndDeletedFalse(Long categoryId, Long brandId, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và brandId và tên chứa chuỗi tìm kiếm mà chưa bị xóa
    Page<Product> findByCategoryIdAndBrandIdAndNameContainingIgnoreCaseAndDeletedFalse(Long categoryId, Long brandId, String name, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và chưa bị xóa
    Page<Product> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);

    // Tìm kiếm sản phẩm theo danh sách các thể loại mà chưa bị xóa
    List<Product> findByCategoryInAndDeletedFalse(Set<Category> categories);

    // Tìm kiếm sản phẩm theo categoryId và tên chứa chuỗi tìm kiếm mà chưa bị xóa
    Page<Product> findByCategoryIdAndNameContainingAndDeletedFalse(Long categoryId, String name, Pageable pageable);

    // Tổng giá của từng sản phẩm dựa theo tên (hoặc ID sản phẩm) mà chưa bị xóa
    @Query("SELECT p.name, SUM(p.price) AS total FROM Product p WHERE p.deleted = false GROUP BY p.name")
    List<Object[]> findTotalByProductName();

    // Tổng giá của tất cả sản phẩm mà chưa bị xóa
    @Query("SELECT SUM(p.price) AS total FROM Product p WHERE p.deleted = false")
    Double findTotalAllProducts();

    // Lấy danh sách sản phẩm ngẫu nhiên mà chưa bị xóa
    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY RAND()")
    List<Product> findRandomProducts(Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId và khoảng giá mà chưa bị xóa
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.price BETWEEN ?2 AND ?3 AND p.deleted = false")
    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo brandId và khoảng giá mà chưa bị xóa
    @Query("SELECT p FROM Product p WHERE p.brand.id = ?1 AND p.price BETWEEN ?2 AND ?3 AND p.deleted = false")
    Page<Product> findByBrandIdAndPriceBetween(Long brandId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo categoryId, brandId và khoảng giá mà chưa bị xóa
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.brand.id = ?2 AND p.price BETWEEN ?3 AND ?4 AND p.deleted = false")
    Page<Product> findByCategoryIdAndBrandIdAndPriceBetween(Long categoryId, Long brandId, Long minPrice, Long maxPrice, Pageable pageable);

    // Tìm kiếm sản phẩm theo khoảng giá mà chưa bị xóa
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2 AND p.deleted = false")
    Page<Product> findByPriceBetween(Long minPrice, Long maxPrice, Pageable pageable);
}