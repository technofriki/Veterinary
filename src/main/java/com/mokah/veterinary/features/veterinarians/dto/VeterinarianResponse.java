package com.mokah.veterinary.features.veterinarians.dto;

import com.mokah.veterinary.features.branches.dto.BranchResponse;

public record VeterinarianResponse(
        String firstName,
        String lastName,
        String licenseNumber,
        String phone,
        String email,
        BranchResponse branch
) {
}
