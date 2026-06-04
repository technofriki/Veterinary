package com.mokah.veterinary.features.adresses.dto;

public record AddressResponse(
        Long id,
        String street,
        String number,
        String city,
        String province,
        String country
) {
}
