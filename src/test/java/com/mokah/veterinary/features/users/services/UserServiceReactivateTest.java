package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceReactivateTest {

    private static final String EMAIL = "client@veterinary.com";
    private static final String REVOKED_REFRESH_TOKEN = "REVOKED-old-token";
    private static final String ADMIN_USERNAME = "admin";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        ADMIN_USERNAME,
                        null,
                        Arrays.stream(new String[]{"UPDATE_USERS"})
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
        );
    }

    @Test
    void reactivateMarksUserActiveAndEnablesCredentials() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.INACTIVE);
        Credentials credentials = credentials(user, false);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));

        authenticateAsAdmin();
        userService.reactivate(externalId);

        assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
        assertThat(credentials.getEnabled()).isTrue();
        assertThat(credentials.getRefreshToken())
                .isNotEqualTo(REVOKED_REFRESH_TOKEN)
                .startsWith("REVOKED-");
        verify(userRepository).save(user);
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void reactivateWithoutCredentialsOnlyMarksUserActive() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.INACTIVE);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.empty());

        authenticateAsAdmin();
        userService.reactivate(externalId);

        assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
        verify(userRepository).save(user);
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void reactivateAlreadyActiveUserThrowsAndDoesNotSave() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.ACTIVE);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));

        authenticateAsAdmin();
        assertThatThrownBy(() -> userService.reactivate(externalId))
                .isInstanceOf(BusinessRuleException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(credentialsRepository);
    }

    @Test
    void reactivateMissingUserThrowsNotFound() {
        UUID externalId = UUID.randomUUID();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        authenticateAsAdmin();
        assertThatThrownBy(() -> userService.reactivate(externalId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(credentialsRepository);
    }

    private User user(UUID externalId, UserState userState) {
        return User.builder()
                .externalId(externalId)
                .email(EMAIL)
                .userState(userState)
                .build();
    }

    private Credentials credentials(User user, boolean enabled) {
        return Credentials.builder()
                .id(1L)
                .username(EMAIL)
                .password("encoded-password")
                .enabled(enabled)
                .refreshToken(REVOKED_REFRESH_TOKEN)
                .user(user)
                .build();
    }
}
