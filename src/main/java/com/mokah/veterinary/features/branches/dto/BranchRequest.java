package com.mokah.veterinary.features.branches.dto;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record BranchRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 20) String phone,
        @NotBlank @Email @Size(max = 50) String email,
        @NotNull LocalTime openingTime,
        @NotNull LocalTime closingTime,
        @NotNull @Valid AddressRequest address
) {}
