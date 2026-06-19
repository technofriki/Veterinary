package com.mokah.veterinary.features.veterinarians.dto;

import com.mokah.veterinary.features.branches.dto.BranchResponse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
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
        Set<DayOfWeek> workDays,
        Boolean active,
        BranchResponse branch
) {
}
