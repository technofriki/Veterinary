package com.mokah.veterinary.features.ownersbypets.dto;

import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.pets.dto.PetResponse;

public record OwnerByPetResponse(
        OwnerResponse owner,
        PetResponse pet
) {
}
