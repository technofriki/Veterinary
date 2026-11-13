package com.mokah.veterinary.features.breed.dto;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;

import java.util.UUID;

public record BreedResponse(
        UUID externalId,
        String name,
        AnimalTypeResponse animalType
) {
}
