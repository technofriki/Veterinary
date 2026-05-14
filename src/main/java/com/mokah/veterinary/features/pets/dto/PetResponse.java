package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponse {
    private Long id;
    private String name;
    private Date birthDate;
    private AnimalTypeEntity animalType;
    private BreedEntity breed;
}
