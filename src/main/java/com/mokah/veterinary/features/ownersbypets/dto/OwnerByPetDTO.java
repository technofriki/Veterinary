package com.mokah.veterinary.features.ownersbypets.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OwnerByPetDTO(
        @NotNull @Positive Long ownerId,
        @NotNull @Positive Long petId
) {
}
