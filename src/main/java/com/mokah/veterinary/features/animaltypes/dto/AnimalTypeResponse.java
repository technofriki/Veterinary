package com.mokah.veterinary.features.animaltypes.dto;

import java.util.UUID;

public record AnimalTypeResponse(
        UUID externalId,
        String name
) {
}
