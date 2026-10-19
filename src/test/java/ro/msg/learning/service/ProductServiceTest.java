package ro.msg.learning.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.msg.learning.dto.ProductDTO;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;
import ro.msg.learning.exception.ResourceNotFoundException;
import ro.msg.learning.mapper.ProductMapper;
import ro.msg.learning.repository.ProductCategoryRepository;
import ro.msg.learning.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductMapper productMapper;


    private ProductService productService;

    private UUID productId;
    private UUID categoryId;
    private Product product;
    private ProductCategory category;
    private ProductDTO productDTO;


    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productCategoryRepository, productMapper);

        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new ProductCategory();
        category.setId(categoryId);
        category.setName("Electronics");

        product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setDescription("A high-performance laptop");
        product.setPrice(new BigDecimal("1500.00"));
        product.setWeight(2.5);
        product.setImageURL("http://example.com/laptop.jpg");
        product.setCategory(category);

        productDTO = ProductDTO.builder()
                .id(productId)
                .name("Laptop")
                .description("A high-performance laptop")
                .price(new BigDecimal("1500.00"))
                .weight(2.5)
                .imageURL("http://example.com/laptop.jpg")
                .categoryId(categoryId)
                .build();
    }


    //create
    @Test
    void createProduct_WhenCategoryExists() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(productDTO, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.ProductToDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);
        assertEquals(productDTO, result);
        verify(productMapper).toEntity(productDTO, category);
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productDTO));

        verify(productRepository, never()).save(any());
    }


    //read
    @Test
    void getById_WhenProductExists_ShouldReturnProductDTO() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.ProductToDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getById(productId);
        assertEquals(productDTO, result);
        verify(productMapper).ProductToDTO(product);
    }

    @Test
    void getById_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(productId));

        verify(productMapper, never()).ProductToDTO(any());
    }

    @Test
    void getAll_ShouldReturnListOfProductDTOs() {
        List<Product> products = List.of(product);
        List<ProductDTO> productDTOs = List.of(productDTO);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.ProductToDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getAll();
        assertEquals(productDTOs, result);
        verify(productRepository).findAll();
        verify(productMapper).ProductToDTO(product);
    }


    //update
    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProductDTO() {
        ProductDTO updatedProductDTO = ProductDTO.builder()
                .id(productId)
                .name("Updated Laptop")
                .description("An updated high-performance laptop")
                .price(new BigDecimal("1600.00"))
                .weight(2.4)
                .imageURL("http://example.com/updated-laptop.jpg")
                .categoryId(categoryId)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.ProductToDTO(product)).thenReturn(updatedProductDTO);

        ProductDTO result = productService.update(productId, updatedProductDTO);
        assertEquals(updatedProductDTO, result);
        assertEquals("Updated Laptop", product.getName());
        assertEquals("An updated high-performance laptop", product.getDescription());
        assertEquals(new BigDecimal("1600.00"), product.getPrice());
        assertEquals(2.4, product.getWeight());
        assertEquals("http://example.com/updated-laptop.jpg", product.getImageURL());
        assertEquals(category, product.getCategory());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, productDTO));

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, productDTO));

        verify(productRepository, never()).save(any());
    }

    //delete
    @Test
    void deleteProductById_WhenProductExists_ShouldDeleteProduct() {
        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProductById(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteProductById_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(productId));

        verify(productRepository, never()).deleteById(productId);
    }


}
