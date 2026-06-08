package com.mokah.veterinary.features.diagnosis.dto;

import com.mokah.veterinary.features.visits.dto.VisitResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiagnosisResponse(
        UUID externalId,
        String description,
        LocalDateTime dateDiagnosis,
        String observations,
        VisitResponse visit
) {
}
