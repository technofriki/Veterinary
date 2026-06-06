package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConditionByPetUpdateDTO(
        @NotNull LocalDateTime diagnosisDate,
        @NotNull ConditionSeverity severity,
        String observations
) {
}
