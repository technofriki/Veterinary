package com.mokah.veterinary.features.prescriptions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrescriptionRequest(
        @NotNull@Positive Long quantity,
        @NotBlank String indication,
        @NotNull Long medicationId,
        @NotNull Long diagnosisId

) {
}
