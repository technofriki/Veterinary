package com.mokah.veterinary.features.diagnosisbystudies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiagnosisByStudyDTO(
        @NotNull Long diagnosisId,
        @NotNull Long studyId,
        @NotBlank String studyConclusions
) {
}
