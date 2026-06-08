package com.mokah.veterinary.features.conditions.dto;

import java.util.UUID;

public record ConditionResponse(
        UUID externalId,
        String name,
        String description
) {
}
