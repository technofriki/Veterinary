package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

public record PetResponse(
        UUID externalId,
        String name,
        LocalDate birthDate,
        AnimalType animalType,
        BreedEntity breed
) {

}
