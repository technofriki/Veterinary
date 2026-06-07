package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.breed.model.Breed;

import java.time.LocalDate;
import java.util.UUID;

public record PetResponse(
        UUID externalId,
        String name,
        LocalDate birthDate,
        AnimalType animalType,
        Breed breed
) {
}
