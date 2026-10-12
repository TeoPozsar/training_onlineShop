package ro.msg.learning.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class StockId implements Serializable {
    private UUID product;
    private UUID location;
}