package com.mokah.veterinary.features.owners.dto;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OwnerRequest(
        @NotBlank @Size(min = 2, max = 30) String firstName,
        @NotBlank @Size(min = 2, max = 30) String lastName,
        @NotBlank @Size(min = 8, max = 20) String phone,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 7, max = 15) String dni,
        @Valid AddressRequest address
) {}