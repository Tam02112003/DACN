package DACN.DACN.services;

import DACN.DACN.entity.Category;
import DACN.DACN.entity.Product;
import DACN.DACN.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Page<Category> getCategories(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return categoryRepository.findByNameContainingIgnoreCase(search,pageable);
    }
    public Category addCategory(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (category != null) {
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
