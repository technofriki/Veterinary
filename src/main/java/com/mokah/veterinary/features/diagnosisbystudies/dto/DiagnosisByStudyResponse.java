package com.mokah.veterinary.features.diagnosisbystudies.dto;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.studies.dto.StudyResponse;

import java.util.UUID;

public record DiagnosisByStudyResponse(
        UUID externalId,
        DiagnosisResponse diagnosis,
        StudyResponse study,
        String studyConclusions,
        Boolean active
) {
}
