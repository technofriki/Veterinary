package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import com.mokah.veterinary.features.pets.dto.PetResponse;

import java.time.LocalDateTime;

public record ConditionByPetResponse(
        ConditionResponse condition,
        PetResponse pet,
        LocalDateTime diagnosisDate,
        ConditionSeverity severity,
        String observations,
        Boolean active
) {
}
