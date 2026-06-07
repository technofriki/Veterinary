package com.mokah.veterinary.features.studiesbyvisit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StudyByVisitDTO(
        @NotNull @Positive Long studyId,
        @NotNull @Positive Long visitId
) {
}
