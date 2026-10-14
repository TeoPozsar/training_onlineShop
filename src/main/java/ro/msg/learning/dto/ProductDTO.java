package ro.msg.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import ro.msg.learning.entity.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
public class ProductDTO {

    private UUID id;

    private String name;
    private String description;
    private BigDecimal price;
    private Double weight;
    private ProductCategory category;
    private String imageURL;

    private UUID categoryId;

    private String categoryName;

    private String categoryDescription;

}
