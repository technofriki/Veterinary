package com.mokah.veterinary.features.users.services;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceReactivateSecurityTest {

    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final String ADMIN_USERNAME = "admin";
    private static final String CLIENT_USERNAME = "client";

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

    private void authenticateAs(String username, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Arrays.stream(authorities)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
        );
    }

    private User user(UUID externalId, UserState userState) {
        return User.builder()
                .externalId(externalId)
                .email("user@veterinary.com")
                .userState(userState)
                .build();
    }

    @Test
    void adminWithUpdateUsersPermissionCanReactivateUser() {
        User user = user(EXTERNAL_ID, UserState.INACTIVE);
        when(userRepository.findByExternalId(EXTERNAL_ID)).thenReturn(Optional.of(user));

        authenticateAs(ADMIN_USERNAME, "UPDATE_USERS");

        userService.reactivate(EXTERNAL_ID);

        verify(userRepository).save(user);
    }

    @Test
    void userWithoutUpdateUsersPermissionCannotReactivateUser() {
        authenticateAs(CLIENT_USERNAME, "VIEW_USERS");

        assertThatThrownBy(() -> userService.reactivate(EXTERNAL_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void clientRoleCannotReactivateUser() {
        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");

        assertThatThrownBy(() -> userService.reactivate(EXTERNAL_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void unauthenticatedRequestCannotReactivateUser() {
        // No authentication set

        assertThatThrownBy(() -> userService.reactivate(EXTERNAL_ID))
                .isInstanceOf(AccessDeniedException.class);

        verify(userRepository, never()).save(any(User.class));
    }
}
