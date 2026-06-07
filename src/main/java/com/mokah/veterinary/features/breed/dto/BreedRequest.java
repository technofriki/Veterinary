package com.mokah.veterinary.features.breed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BreedRequest(
        @NotBlank @Size(min = 1, max = 50) String name,
        @NotBlank @Size(min = 1, max = 50) String color
) {
}
