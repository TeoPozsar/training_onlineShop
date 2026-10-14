package ro.msg.learning.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ro.msg.learning.dto.ProductDTO;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.exception.ResourceNotFoundException;
import ro.msg.learning.mapper.ProductMapper;
import ro.msg.learning.repository.ProductCategoryRepository;
import ro.msg.learning.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository productCategoryRepository;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAll()
                .stream()
                .map(productMapper::ProductToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable UUID id) {
        Product product = productService.getById(id);
        return productMapper.ProductToDTO(product);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody ProductDTO productDto) {

        ProductCategory category = productCategoryRepository
                .findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product category not found: " + productDto.getCategoryId()));

        Product product = productMapper.toEntity(productDto, category);
        product.setId(null);

        Product savedProduct = productService.createProduct(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productMapper.ProductToDTO(savedProduct));
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductDTO productDto) {

        ProductCategory category = productCategoryRepository
                .findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product category not found: " + productDto.getCategoryId()));

        Product updatedProduct = productMapper.toEntity(productDto, category);

        Product savedProduct = productService.update(id, updatedProduct);

        return productMapper.ProductToDTO(savedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}