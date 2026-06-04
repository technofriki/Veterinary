package com.mokah.veterinary.features.branches.dto;

import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import lombok.*;

public record BranchResponse(
        Long id,
        String name,
        String phone,
        String email,
        AddressResponse address) {
}
