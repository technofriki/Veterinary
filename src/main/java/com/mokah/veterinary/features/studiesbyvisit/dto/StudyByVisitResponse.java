package com.mokah.veterinary.features.studiesbyvisit.dto;

import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.visits.dto.VisitResponse;

public record StudyByVisitResponse(
        StudyResponse study,
        VisitResponse visit,
        Boolean active
) {
}
