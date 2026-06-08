package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import com.mokah.veterinary.features.pets.dto.PetResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConditionByPetResponse(
        UUID externalId,
        ConditionResponse condition,
        PetResponse pet,
        LocalDateTime diagnosisDate,
        ConditionSeverity severity,
        String observations,
        Boolean active
) {
}
