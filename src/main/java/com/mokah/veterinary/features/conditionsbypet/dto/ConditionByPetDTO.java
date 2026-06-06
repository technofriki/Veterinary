package com.mokah.veterinary.features.conditionsbypet.dto;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionSerevity;

import java.time.LocalDateTime;

public record ConditionByPetDTO(
        Long conditionId,
        Long petId,
        LocalDateTime diagnosisDate,
        ConditionSerevity serevity,
        String observations,
        Boolean active
) {
}
