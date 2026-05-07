package com.mokah.veterinary.features.breed.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "breed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "color", nullable = false, length = 50)
    private String color;
}
