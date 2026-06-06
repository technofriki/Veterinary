package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConditionByPetDTO(
       @NotNull Long conditionId,
       @NotNull Long petId,
       @NotNull LocalDateTime diagnosisDate,
       @NotNull ConditionSeverity severity,
        String observations
) {
}
