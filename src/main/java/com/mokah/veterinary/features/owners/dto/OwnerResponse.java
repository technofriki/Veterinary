package com.mokah.veterinary.features.owners.dto;

import com.mokah.veterinary.features.adresses.dto.AddressResponse;

import java.util.UUID;

public record OwnerResponse(
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        String email,
        String dni,
        AddressResponse address
) {}
