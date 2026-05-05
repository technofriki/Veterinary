package com.mokah.veterinary.features.animaltypes.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "animal_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalTypeEntity {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column (name = "name", nullable = true, length = 50)
    private String animalType;

}
