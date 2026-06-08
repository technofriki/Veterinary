package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConditionByPetDTO(
        @NotNull UUID conditionExternalId,
        @NotNull UUID petExternalId,
        @NotNull LocalDateTime diagnosisDate,
        @NotNull ConditionSeverity severity,
        String observations
) {
}