package com.mokah.veterinary.features.adresses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 100)
        String street,

        @NotBlank(message = "Street number is required")
        @Size(max = 50)
        String number,

        @NotBlank(message = "City number is required")
        @Size(max = 50)
        String city,

        @NotBlank(message = "Province is required")
        @Size(max = 50)
        String province,

        @NotBlank(message = "Country is required")
        @Size(max = 50)
        String country) {
}
