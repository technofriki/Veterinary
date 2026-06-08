package com.mokah.veterinary.security.dto;

public record AuthResponse(

        String accessToken,
        String refreshToken

) {
}