package com.mokah.veterinary.features.branches.dto;

import com.mokah.veterinary.features.adresses.dto.AddressResponse;

import java.time.LocalTime;
import java.util.UUID;

public record BranchResponse(
        UUID externalId,
        String name,
        String phone,
        String email,
        LocalTime openingTime,
        LocalTime closingTime,
        AddressResponse address
) {}
