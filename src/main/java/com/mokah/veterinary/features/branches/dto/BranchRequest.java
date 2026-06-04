package com.mokah.veterinary.features.branches.dto;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


public record BranchRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 50)
        String name,

        @NotBlank(message = "Phone is required")
        @Size(max = 20)
        String phone,


        @NotBlank(message = "Email is required")
        @Email(message = "Invalid format email")
        @Size(max = 50)
        String email,

        @NotNull @Valid
        AddressRequest address
) {
}
