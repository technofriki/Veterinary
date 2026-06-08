package com.mokah.veterinary.features.ownersbypets.dto;

import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.pets.dto.PetResponse;

import java.util.UUID;

public record OwnerByPetResponse(
        UUID externalId,
        OwnerResponse owner,
        PetResponse pet
) {
}
