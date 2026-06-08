package com.mokah.veterinary.security.service;

import com.mokah.veterinary.security.dto.AuthRequest;
import com.mokah.veterinary.security.dto.AuthResponse;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Credentials authenticate(AuthRequest input) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );

        return credentialsRepository
                .findByUsername(input.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {

        String username =
                jwtService.extractUsername(refreshToken);

        Credentials user =
                credentialsRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        if (!user.getRefreshToken().equals(refreshToken)) {

            throw new IllegalArgumentException(
                    "Refresh token does not match"
            );
        }

        if (!jwtService.validateRefreshToken(
                refreshToken,
                user
        )) {

            throw new IllegalArgumentException(
                    "Refresh token expired or invalid"
            );
        }

        String newAccessToken =
                jwtService.generateToken(user);

        String newRefreshToken =
                jwtService.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);

        credentialsRepository.save(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken
        );
    }
}
