package com.mokah.veterinary.security.service;

import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.dto.AuthRequest;
import com.mokah.veterinary.security.dto.AuthResponse;
import com.mokah.veterinary.security.dto.RegisterRequest;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (credentialsRepository.findByUsername(request.email()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .userState(UserState.ACTIVE)
                .build();

        user = userRepository.save(user);

        Role defaultRole = roleRepository.findByRole(Roles.ROLE_CLIENT)
                .orElseThrow(() -> new IllegalStateException("Default role not found"));

        String encodedPassword = passwordEncoder.encode(request.password());

        Credentials credentials = Credentials.builder()
                .username(request.email())
                .password(encodedPassword)
                .enabled(true)
                .user(user)
                .roles(Set.of(defaultRole))
                .build();

        String refreshToken = jwtService.generateRefreshToken(credentials);

        credentials.setRefreshToken(refreshToken);

        credentialsRepository.save(credentials);
    }
}
