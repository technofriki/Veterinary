package com.mokah.veterinary.features.breed.dto;

import java.util.UUID;

public record BreedResponse(
        UUID externalId,
        String name,
        String color
) {
}
