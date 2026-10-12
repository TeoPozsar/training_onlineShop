package ro.msg.learning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "location")
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address_country", nullable = false)
    private String addressCountry;

    @Column(name = "address_city", nullable = false)
    private String addressCity;

    @Column(name = "address_county", nullable = false)
    private String addressCounty;

    @Column(name = "address_street", nullable = false)
    private String addressStreet;

}

