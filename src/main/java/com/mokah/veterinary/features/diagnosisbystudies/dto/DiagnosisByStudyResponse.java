package com.mokah.veterinary.features.diagnosisbystudies.dto;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.studies.dto.StudyResponse;

public record DiagnosisByStudyResponse(
        DiagnosisResponse diagnosis,
        StudyResponse study,
        String studyConclusions,
        Boolean active
) {
}
