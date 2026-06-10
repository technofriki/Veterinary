package com.mokah.veterinary.features.prescriptions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record PrescriptionRequest(
        @NotNull @Positive Long quantity,
        @NotBlank String indication,
        @NotNull LocalDate expirationDate,
        @NotNull UUID medicationExternalId,
        @NotNull UUID diagnosisExternalId
) {
}
