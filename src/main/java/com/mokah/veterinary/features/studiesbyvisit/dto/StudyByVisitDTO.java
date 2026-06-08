package com.mokah.veterinary.features.studiesbyvisit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StudyByVisitDTO(
        @NotNull UUID studyExternalId,
        @NotNull UUID visitExternalId
) {
}
