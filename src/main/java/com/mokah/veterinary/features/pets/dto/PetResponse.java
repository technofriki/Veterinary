package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponse {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private AnimalTypeEntity animalType;
    private BreedEntity breed;
}
