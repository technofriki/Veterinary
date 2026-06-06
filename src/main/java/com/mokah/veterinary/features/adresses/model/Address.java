package com.mokah.veterinary.features.adresses.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street", nullable = false,  length = 100)
    private String street;

    @Column(name = "number", nullable = false, length = 50)
    private String number;

    @Column(name = "city", nullable = true, length = 50)
    private String city;

    @Column(name = "province", nullable = true, length = 50)
    private String province;

    @Column(name = "country", nullable = true, length = 50)
    private String country;
}
