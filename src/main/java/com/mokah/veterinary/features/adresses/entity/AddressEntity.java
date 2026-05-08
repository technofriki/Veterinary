package com.mokah.veterinary.features.adresses.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "adress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street", nullable = false,  length = 100)
    private String street;

    @Column(name = "street_number", nullable = false, length = 50)
    private String streetNumber;

    @Column(name = "city", nullable = true, length = 50)
    private String city;

    @Column(name = "province", nullable = true, length = 50)
    private String province;

    @Column(name = "country", nullable = true, length = 50)
    private String country;
}
