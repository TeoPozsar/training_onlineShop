package ro.msg.learning.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.msg.learning.dto.ProductDTO;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.exception.ResourceNotFoundException;
import ro.msg.learning.mapper.ProductMapper;
import ro.msg.learning.repository.ProductCategoryRepository;
import ro.msg.learning.repository.ProductRepository;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductCategory category = getCategoryById(productDTO.getCategoryId());
        Product product = productMapper.toEntity(productDTO, category);
        product.setId(null);
        Product savedProduct = productRepository.save(product);
        return productMapper.ProductToDTO(savedProduct);
    }

    public ProductDTO update(UUID id, ProductDTO updatedProductDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));

        ProductCategory category = getCategoryById(updatedProductDTO.getCategoryId());
        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setWeight(updatedProductDTO.getWeight());
        existingProduct.setImageURL(updatedProductDTO.getImageURL());
        existingProduct.setCategory(category);

        Product savedProduct = productRepository.save(existingProduct);
        return productMapper.ProductToDTO(savedProduct);
    }

    public void deleteProductById(UUID id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
    }

    @Transactional
    public ProductDTO getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));
        return productMapper.ProductToDTO(product);
    }

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::ProductToDTO)
                .toList();
    }

    public ProductCategory getCategoryById(UUID categoryId) {
        return productCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product category not found with id: " + categoryId));
    }


}
