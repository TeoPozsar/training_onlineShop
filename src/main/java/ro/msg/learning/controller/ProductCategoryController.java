package ro.msg.learning.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.msg.learning.dto.ProductCategoryDTO;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.mapper.ProductCategoryMapper;
import ro.msg.learning.service.ProductCategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    private final ProductCategoryMapper productCategoryMapper;

    @GetMapping
    public List<ProductCategoryDTO> getAllCategories() {
        return productCategoryService.getAllCategories()
                .stream()
                .map(productCategoryMapper::CategoryToDTO).toList();
    }

    @GetMapping("/{id}")
    public ProductCategoryDTO getCategoryById(@PathVariable UUID id) {
        ProductCategory category = productCategoryService.getCategoryById(id);
        return productCategoryMapper.CategoryToDTO(category);
    }

    @PostMapping
    public ProductCategoryDTO createCategory(@RequestBody ProductCategoryDTO categoryDto) {
        ProductCategory category = productCategoryMapper.toEntity(categoryDto);
        ProductCategory createdCategory = productCategoryService.createCategory(category);
        return productCategoryMapper.CategoryToDTO(createdCategory);
    }

    @PutMapping("/{id}")
    public ProductCategoryDTO updateCategory(@PathVariable UUID id, @RequestBody ProductCategoryDTO categoryDto) {
        ProductCategory updatedCategory = productCategoryMapper.toEntity(categoryDto);
        ProductCategory category = productCategoryService.updateCategory(id, updatedCategory);
        return productCategoryMapper.CategoryToDTO(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        productCategoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
