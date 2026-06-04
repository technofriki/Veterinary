package com.mokah.veterinary.features.pets.entity;

import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table (name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "animal_type_id")
    private AnimalTypeEntity animalType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;
}
