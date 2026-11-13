package com.mokah.veterinary.features.pets.dto;

import jakarta.validation.constraints.Size;

public record PetDescriptionUpdateDTO(
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
