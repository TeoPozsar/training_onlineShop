package ro.msg.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ProductCategoryDTO {
    private UUID id;
    private String name;
    private String description;
}
