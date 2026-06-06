package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionSerevity;
import com.mokah.veterinary.features.pets.dto.PetResponse;

import java.time.LocalDateTime;

public record ConditionByPetResponse(
        ConditionResponse condition,
        PetResponse pet,
        LocalDateTime diagnosisDate,
        ConditionSerevity serevity,
        String observations,
        Boolean active
) {
}
