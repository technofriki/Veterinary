package com.mokah.veterinary.features.studiesbyvisit.dto;

import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.visits.dto.VisitResponse;

import java.util.UUID;

public record StudyByVisitResponse(
        UUID externalId,
        StudyResponse study,
        VisitResponse visit,
        Boolean active
) {
}
