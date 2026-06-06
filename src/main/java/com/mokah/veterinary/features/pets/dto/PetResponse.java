package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponse {
    private String name;
    private LocalDate birthDate;
    private AnimalType animalType;
    private BreedEntity breed;
}
