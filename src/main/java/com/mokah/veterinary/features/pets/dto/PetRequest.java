package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.breed.dto.BreedRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record PetRequest(
        @NotBlank String name,
        LocalDate birthDate,
        @NotBlank String color,
        @NotNull @Valid AnimalTypeRequest animalType,
        @NotNull @Valid BreedRequest breed
) {
}
