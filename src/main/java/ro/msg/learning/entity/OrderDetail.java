package ro.msg.learning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "order_details")
@Getter
@Setter
@IdClass(OrderDetailId.class)
public class OrderDetail {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipped_from_location_id", nullable = false)
    private Location shippedFromLocation;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}

