package com.mokah.veterinary.features.pets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PetRequest(
        @NotBlank String name,
        LocalDate birthDate,
        @NotBlank String color,
        @NotNull UUID animalTypeId,
        @NotNull UUID breedId
) {
}
