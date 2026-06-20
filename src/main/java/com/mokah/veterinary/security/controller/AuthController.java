package com.mokah.veterinary.security.controller;

import com.mokah.veterinary.security.dto.AuthRequest;
import com.mokah.veterinary.security.dto.AuthResponse;
import com.mokah.veterinary.security.dto.RefreshTokenRequest;
import com.mokah.veterinary.security.dto.RegisterRequest;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.service.AuthService;
import com.mokah.veterinary.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest) {
        Credentials user = authService.authenticate(authRequest);

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, user.getRefreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
}