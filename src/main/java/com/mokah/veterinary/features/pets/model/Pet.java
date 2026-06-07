package com.mokah.veterinary.features.pets.model;

import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;
    @PrePersist
    public void generateExternalid() {
        if (externalId == null){
            externalId = UUID.randomUUID();
        }
    }

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "animal_type_id")
    private AnimalType animalType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;
}
