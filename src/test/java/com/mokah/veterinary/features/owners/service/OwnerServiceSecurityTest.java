package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.common.exception.OwnerDniExistsException;
import com.mokah.veterinary.common.exception.ResourceAlreadyExistsException;
import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.model.Address;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.dto.OwnerWithCredentialsRequest;
import com.mokah.veterinary.features.owners.mapper.OwnerMapper;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import com.mokah.veterinary.security.service.AuthService;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceSecurityTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String RECEPTIONIST_USERNAME = "receptionist@veterinary.com";
    private static final String CLIENT_USERNAME = "client@veterinary.com";

    @Mock
    private OwnerRepository repository;

    @Mock
    private OwnerMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.mokah.veterinary.features.adresses.repository.AddressRepository addressRepository;

    @Mock
    private com.mokah.veterinary.features.adresses.mapper.AddressMapper addressMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private OwnerServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String username, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        java.util.Arrays.stream(authorities)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
        );
    }

    private OwnerWithCredentialsRequest ownerWithCredentialsRequest() {
        AddressRequest addressRequest = new AddressRequest("Street", "123", "City", "Province", "Country");
        return new OwnerWithCredentialsRequest(
                "John",
                "Doe",
                "12345678",
                "john.doe@example.com",
                "12345678",
                addressRequest,
                "Password123!"
        );
    }

    private Owner owner() {
        return Owner.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .phone("12345678")
                .dni("12345678")
                .user(User.builder().id(1L).build())
                .address(Address.builder().id(1L).build())
                .build();
    }

    @Test
    void adminCanCreateOwnerWithCredentials() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();
        Owner savedOwner = owner();

        authenticateAs(ADMIN_USERNAME, "CREATE_OWNERS");
        when(repository.existsByDni(request.dni())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedOwner.getUser());
        when(authService.createCredentialsForUser(any(User.class), anyString(), isNull())).thenReturn(mock(Credentials.class));
        when(addressMapper.toEntity(any(AddressRequest.class))).thenReturn(savedOwner.getAddress());
        when(addressRepository.save(any(Address.class))).thenReturn(savedOwner.getAddress());
        when(repository.save(any(Owner.class))).thenReturn(savedOwner);
        when(mapper.toResponse(savedOwner)).thenReturn(new OwnerResponse(
                savedOwner.getExternalId(),
                "John",
                "Doe",
                "12345678",
                "john.doe@example.com",
                "12345678",
                null
        ));

        OwnerResponse response = service.createWithCredentials(request);

        assertThat(response).isNotNull();
        verify(authService).createCredentialsForUser(any(User.class), eq(request.password()), isNull());
        verify(repository).save(any(Owner.class));
    }

    @Test
    void receptionistWithCreateOwnersPermissionCanCreateOwnerWithCredentials() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();
        Owner savedOwner = owner();

        authenticateAs(RECEPTIONIST_USERNAME, "CREATE_OWNERS");
        when(repository.existsByDni(request.dni())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedOwner.getUser());
        when(authService.createCredentialsForUser(any(User.class), anyString(), isNull())).thenReturn(mock(Credentials.class));
        when(addressMapper.toEntity(any(AddressRequest.class))).thenReturn(savedOwner.getAddress());
        when(addressRepository.save(any(Address.class))).thenReturn(savedOwner.getAddress());
        when(repository.save(any(Owner.class))).thenReturn(savedOwner);
        when(mapper.toResponse(savedOwner)).thenReturn(new OwnerResponse(
                savedOwner.getExternalId(),
                "John",
                "Doe",
                "12345678",
                "john.doe@example.com",
                "12345678",
                null
        ));

        OwnerResponse response = service.createWithCredentials(request);

        assertThat(response).isNotNull();
        verify(authService).createCredentialsForUser(any(User.class), eq(request.password()), isNull());
    }

    @Test
    void userWithoutCreateOwnersPermissionCannotCreateOwnerWithCredentials() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");

        assertThatThrownBy(() -> service.createWithCredentials(request))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
        verify(authService, never()).createCredentialsForUser(any(), anyString(), any());
    }

    @Test
    void duplicateDniThrowsException() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();

        authenticateAs(ADMIN_USERNAME, "CREATE_OWNERS");
        when(repository.existsByDni(request.dni())).thenReturn(true);

        assertThatThrownBy(() -> service.createWithCredentials(request))
                .isInstanceOf(OwnerDniExistsException.class)
                .hasMessageContaining("DNI");

        verify(userRepository, never()).save(any());
        verify(authService, never()).createCredentialsForUser(any(), anyString(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void duplicateEmailThrowsException() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();

        authenticateAs(ADMIN_USERNAME, "CREATE_OWNERS");
        when(repository.existsByDni(request.dni())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> service.createWithCredentials(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any());
        verify(authService, never()).createCredentialsForUser(any(), anyString(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void ownerReceivesOnlyRoleClient() {
        OwnerWithCredentialsRequest request = ownerWithCredentialsRequest();
        Owner savedOwner = owner();

        authenticateAs(ADMIN_USERNAME, "CREATE_OWNERS");
        when(repository.existsByDni(request.dni())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedOwner.getUser());
        when(addressMapper.toEntity(any(AddressRequest.class))).thenReturn(savedOwner.getAddress());
        when(addressRepository.save(any(Address.class))).thenReturn(savedOwner.getAddress());
        when(repository.save(any(Owner.class))).thenReturn(savedOwner);
        when(mapper.toResponse(savedOwner)).thenReturn(new OwnerResponse(
                savedOwner.getExternalId(),
                "John",
                "Doe",
                "12345678",
                "john.doe@example.com",
                "12345678",
                null
        ));

        service.createWithCredentials(request);

        verify(authService).createCredentialsForUser(any(User.class), eq(request.password()), isNull());
    }
}
