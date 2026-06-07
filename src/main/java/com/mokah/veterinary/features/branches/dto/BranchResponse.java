package com.mokah.veterinary.features.branches.dto;

import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import lombok.*;

import java.util.UUID;

public record BranchResponse(
        UUID externalId,
        String name,
        String phone,
        String email,
        AddressResponse address
) {}
