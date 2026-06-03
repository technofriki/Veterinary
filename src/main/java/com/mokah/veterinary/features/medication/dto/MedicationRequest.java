package com.mokah.veterinary.features.medication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicationRequest(
        @NotBlank String name,
        @NotNull @Positive Double dosage,
        @NotBlank String dosageUnit
) {
}
