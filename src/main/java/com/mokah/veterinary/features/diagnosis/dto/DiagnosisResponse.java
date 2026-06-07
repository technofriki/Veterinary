package com.mokah.veterinary.features.diagnosis.dto;

import com.mokah.veterinary.features.visits.entity.VisitEntity;
import lombok.*;

import java.time.LocalDateTime;

public record DiagnosisResponse(
        String description,
        LocalDateTime dateDiagnosis,
        String observations,
        VisitEntity visit) {
}
