package com.mokah.veterinary.features.veterinarians.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.UUID;

public record VeterinarianUpdateDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String licenseNumber,
        @NotBlank String phone,
        @NotNull LocalTime workStartTime,
        @NotNull LocalTime workEndTime,
        @NotBlank @Email String email,
        UUID branchExternalId
) {}
