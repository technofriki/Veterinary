package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceDeleteTest {

    private static final String EMAIL = "client@veterinary.com";
    private static final String VALID_REFRESH_TOKEN = "valid-refresh-token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void deleteMarksUserInactiveAndRevokesCredentials() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.ACTIVE);
        Credentials credentials = credentials(user, true);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));

        userService.delete(externalId);

        assertThat(user.getUserState()).isEqualTo(UserState.INACTIVE);
        assertThat(credentials.getEnabled()).isFalse();
        assertThat(credentials.getRefreshToken())
                .isNotEqualTo(VALID_REFRESH_TOKEN)
                .startsWith("REVOKED-");
        verify(userRepository).save(user);
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void deleteWithoutCredentialsOnlyMarksUserInactive() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.ACTIVE);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.empty());

        userService.delete(externalId);

        assertThat(user.getUserState()).isEqualTo(UserState.INACTIVE);
        verify(userRepository).save(user);
        verify(credentialsRepository, never()).save(any(Credentials.class));
    }

    @Test
    void deleteAlreadyInactiveUserThrowsAndDoesNotSave() {
        UUID externalId = UUID.randomUUID();
        User user = user(externalId, UserState.INACTIVE);

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.delete(externalId))
                .isInstanceOf(BusinessRuleException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(credentialsRepository);
    }

    @Test
    void deleteMissingUserThrowsNotFound() {
        UUID externalId = UUID.randomUUID();

        when(userRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(externalId))
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
                .refreshToken(VALID_REFRESH_TOKEN)
                .user(user)
                .build();
    }
}
