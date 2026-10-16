package ro.msg.learning.mapper;
import org.springframework.stereotype.Component;
import ro.msg.learning.dto.ProductCategoryDTO;
import ro.msg.learning.entity.ProductCategory;

@Component
public class ProductCategoryMapper {

    public ProductCategoryDTO CategoryToDTO(ProductCategory category) {
        return ProductCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public ProductCategory toEntity(ProductCategoryDTO dto) {
        ProductCategory category = new ProductCategory();

        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        return category;
    }
}
