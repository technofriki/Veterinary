package com.mokah.veterinary.features.diagnosis.dto;

import com.mokah.veterinary.features.visits.dto.VisitResponse;

import java.time.LocalDateTime;

public record DiagnosisResponse(
        String description,
        LocalDateTime dateDiagnosis,
        String observations,
        VisitResponse visit) {
}
