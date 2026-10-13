package ro.msg.learning.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class OrderDetailId implements Serializable {
    private UUID order;
    private UUID product;
}
