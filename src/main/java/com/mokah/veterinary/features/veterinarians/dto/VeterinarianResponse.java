package com.mokah.veterinary.features.veterinarians.dto;

import com.mokah.veterinary.features.branches.dto.BranchResponse;

import java.time.LocalTime;
import java.util.UUID;

public record VeterinarianResponse(
        UUID externalId,
        String firstName,
        String lastName,
        String licenseNumber,
        String phone,
        String email,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Boolean active,
        BranchResponse branch
) {
}
