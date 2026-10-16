package ro.msg.learning.mapper;
import org.springframework.stereotype.Component;
import ro.msg.learning.dto.ProductDTO;
import ro.msg.learning.entity.Product;
import ro.msg.learning.entity.ProductCategory;


@Component
public class ProductMapper {

    public ProductDTO ProductToDTO(Product product) {
        ProductCategory category = product.getCategory();

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .weight(product.getWeight())
                .imageURL(product.getImageURL())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryDescription(category.getDescription())
                .build();
    }

    public Product toEntity(ProductDTO dto, ProductCategory category) {
        Product product = new Product();

        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setWeight(dto.getWeight());
        product.setImageURL(dto.getImageURL());
        product.setCategory(category);

        return product;
    }
}
