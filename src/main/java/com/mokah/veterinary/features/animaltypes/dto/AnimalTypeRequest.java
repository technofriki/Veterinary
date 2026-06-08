package com.mokah.veterinary.features.animaltypes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnimalTypeRequest(
        @NotBlank @Size(max = 50) String name
) {
}
