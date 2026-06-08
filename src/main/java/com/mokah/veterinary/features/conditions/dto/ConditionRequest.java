package com.mokah.veterinary.features.conditions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConditionRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 255) String description
) {
}
