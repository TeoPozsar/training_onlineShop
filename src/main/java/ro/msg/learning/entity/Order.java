package ro.msg.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="created_at", nullable = false, insertable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "address_country", nullable = false)
    private String addressCountry;

    @Column(name = "address_city", nullable = false)
    private String addressCity;

    @Column(name = "address_county", nullable = false)
    private String addressCounty;

    @Column(name = "address_street", nullable = false)
    private String addressStreet;

}

