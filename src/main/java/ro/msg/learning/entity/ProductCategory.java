package ro.msg.learning.entity;


import jakarta.persistence.Entity;
import lombok.Getter;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "product_category")
@Getter
@Setter
public class ProductCategory {
    @Id
    private UUID id;

    private String name;

    private String description;
}
