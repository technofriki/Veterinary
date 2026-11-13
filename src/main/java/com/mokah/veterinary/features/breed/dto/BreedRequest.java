package com.mokah.veterinary.features.breed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BreedRequest(
        @NotBlank @Size(min = 1, max = 50) String name,
        @NotNull UUID animalTypeId
) {
}
