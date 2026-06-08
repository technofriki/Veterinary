package com.mokah.veterinary.features.diagnosisbystudies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DiagnosisByStudyDTO(
        @NotNull UUID diagnosisExternalId,
        @NotNull UUID studyExternalId,
        @NotBlank String studyConclusions
) {
}
