package com.mokah.veterinary.security.service;

import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.security.dto.AuthRequest;
import com.mokah.veterinary.security.dto.AuthResponse;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceAccountStateTest {

    private static final String USERNAME = "client@veterinary.com";
    private static final String PASSWORD = "secret";
    private static final String OLD_REFRESH_TOKEN = "old-refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String NEW_REFRESH_TOKEN = "new-refresh-token";

    @Mock
    private CredentialsRepository credentialsRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokensForActiveUser() {
        Credentials credentials = credentials(UserState.ACTIVE, true);

        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));
        when(jwtService.generateToken(credentials)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(credentials)).thenReturn("refresh-token");

        AuthResponse response = authService.login(new AuthRequest(USERNAME, PASSWORD));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(credentials.getRefreshToken()).isEqualTo("refresh-token");
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void loginRejectsInactiveUserWithoutGeneratingOrSavingTokens() {
        Credentials credentials = credentials(UserState.INACTIVE, true);

        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));

        assertThatThrownBy(() -> authService.login(new AuthRequest(USERNAME, PASSWORD)))
                .isInstanceOf(DisabledException.class);

        verifyNoInteractions(jwtService);
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void loginRejectsDisabledCredentialsWithoutGeneratingOrSavingTokens() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User is disabled"));

        assertThatThrownBy(() -> authService.login(new AuthRequest(USERNAME, PASSWORD)))
                .isInstanceOf(DisabledException.class);

        verifyNoInteractions(jwtService);
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void refreshReturnsNewTokensForActiveUser() {
        Credentials credentials = credentials(UserState.ACTIVE, true);

        when(jwtService.extractUsername(OLD_REFRESH_TOKEN)).thenReturn(USERNAME);
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));
        when(jwtService.validateRefreshToken(OLD_REFRESH_TOKEN, credentials)).thenReturn(true);
        when(jwtService.generateToken(credentials)).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(credentials)).thenReturn(NEW_REFRESH_TOKEN);

        AuthResponse response = authService.refreshAccessToken(OLD_REFRESH_TOKEN);

        assertThat(response.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        assertThat(credentials.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void refreshRejectsInactiveUserWithoutGeneratingOrSavingTokens() {
        Credentials credentials = credentials(UserState.INACTIVE, true);

        when(jwtService.extractUsername(OLD_REFRESH_TOKEN)).thenReturn(USERNAME);
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));

        assertThatThrownBy(() -> authService.refreshAccessToken(OLD_REFRESH_TOKEN))
                .isInstanceOf(DisabledException.class);

        verify(jwtService, never()).validateRefreshToken(anyString(), any(Credentials.class));
        verify(jwtService, never()).generateToken(any(Credentials.class));
        verify(jwtService, never()).generateRefreshToken(any(Credentials.class));
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void refreshRejectsDisabledCredentialsWithoutGeneratingOrSavingTokens() {
        Credentials credentials = credentials(UserState.ACTIVE, false);

        when(jwtService.extractUsername(OLD_REFRESH_TOKEN)).thenReturn(USERNAME);
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));

        assertThatThrownBy(() -> authService.refreshAccessToken(OLD_REFRESH_TOKEN))
                .isInstanceOf(DisabledException.class);

        verify(jwtService, never()).validateRefreshToken(anyString(), any(Credentials.class));
        verify(jwtService, never()).generateToken(any(Credentials.class));
        verify(jwtService, never()).generateRefreshToken(any(Credentials.class));
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void refreshRejectsOldTokenAfterReactivation() {
        // Simula que UserService.reactivate() ya cambió el refresh token almacenado
        // a "REVOKED-" + nuevo UUID, invalidando el token anterior
        String revokedToken = "REVOKED-" + UUID.randomUUID();
        Credentials credentials = credentials(UserState.ACTIVE, true);
        credentials.setRefreshToken(revokedToken);

        when(jwtService.extractUsername(OLD_REFRESH_TOKEN)).thenReturn(USERNAME);
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));

        // El token antiguo no coincide con el token almacenado (REVOKED-...)
        assertThatThrownBy(() -> authService.refreshAccessToken(OLD_REFRESH_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match");

        verify(jwtService, never()).generateToken(any(Credentials.class));
        verify(jwtService, never()).generateRefreshToken(any(Credentials.class));
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void loginSucceedsAfterReactivation() {
        // Tras reactivar, el usuario está ACTIVE y enabled=true
        // Login debería funcionar normalmente
        Credentials credentials = credentials(UserState.ACTIVE, true);
        String revokedToken = "REVOKED-" + UUID.randomUUID();
        credentials.setRefreshToken(revokedToken);

        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));
        when(jwtService.generateToken(credentials)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(credentials)).thenReturn("fresh-refresh-token");

        AuthResponse response = authService.login(new AuthRequest(USERNAME, PASSWORD));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("fresh-refresh-token");
        // El refresh token REVOKED- fue reemplazado por uno nuevo
        assertThat(credentials.getRefreshToken()).isEqualTo("fresh-refresh-token");
        verify(credentialsRepository).save(credentials);
    }

    private Credentials credentials(UserState userState, boolean enabled) {
        User user = User.builder()
                .externalId(UUID.randomUUID())
                .email(USERNAME)
                .userState(userState)
                .build();

        return Credentials.builder()
                .id(1L)
                .username(USERNAME)
                .password("encoded-password")
                .enabled(enabled)
                .refreshToken(OLD_REFRESH_TOKEN)
                .user(user)
                .build();
    }
}
