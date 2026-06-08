package com.mokah.veterinary.features.studies.dto;

import java.util.UUID;

public record StudyResponse(
        UUID externalId,
        String name,
        String description
) {
}
