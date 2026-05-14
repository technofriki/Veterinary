package com.mokah.veterinary.features.pets.entity;

import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table (name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_Type_id")
    private AnimalTypeEntity animalType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;
}
