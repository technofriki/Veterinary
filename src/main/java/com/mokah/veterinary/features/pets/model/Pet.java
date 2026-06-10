package com.mokah.veterinary.features.pets.model;

import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.breed.model.Breed;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pets")
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
    public void generateExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID();
        }
    }

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "animal_type_id")
    private AnimalType animalType;

    @ManyToOne
    @JoinColumn(name = "breed_id")
    private Breed breed;
}
