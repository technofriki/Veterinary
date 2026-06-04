package com.mokah.veterinary.features.veterinarians.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VeterinarianCreateDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String licenseNumber,
        @NotBlank String phone,
        @NotBlank @Email String email,
        @NotNull @Positive Long branchId
) {
}
