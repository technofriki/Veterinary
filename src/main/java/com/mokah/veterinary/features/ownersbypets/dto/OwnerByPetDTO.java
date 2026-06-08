package com.mokah.veterinary.features.ownersbypets.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OwnerByPetDTO(
        @NotNull UUID ownerExternalId,
        @NotNull UUID petExternalId
) {
}
