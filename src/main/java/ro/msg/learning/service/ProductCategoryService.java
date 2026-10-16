package ro.msg.learning.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.exception.ResourceNotFoundException;
import ro.msg.learning.repository.ProductCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }

    public ProductCategory getCategoryById(java.util.UUID id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));
    }

    public ProductCategory createCategory(ProductCategory category) {
        category.setId(null);
        return productCategoryRepository.save(category);
    }

    public ProductCategory updateCategory(java.util.UUID id, ProductCategory updatedCategory) {
        ProductCategory existingCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());

        return productCategoryRepository.save(existingCategory);
    }

    public void deleteCategoryById(java.util.UUID id) {
        if (productCategoryRepository.existsById(id)) {
            productCategoryRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product category not found with id: " + id);
        }
    }
}
