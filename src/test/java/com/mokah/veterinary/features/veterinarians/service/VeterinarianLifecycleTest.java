package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeterinarianLifecycleTest {
    @Mock VeterinarianRepository repository;
    @Mock VeterinarianMapper mapper;
    @Mock CredentialsRepository credentialsRepository;
    @Mock RoleRepository roleRepository;
    @InjectMocks VeterinarianServiceImpl service;

    private final Role vetRole = Role.builder().id(1L).role(Roles.ROLE_VETERINARIAN).build();
    private final Role clientRole = Role.builder().id(2L).role(Roles.ROLE_CLIENT).build();
    private User user;
    private Veterinarian vet;
    private Credentials credentials;

    @BeforeEach
    void setup() {
        user = User.builder().id(10L).userState(UserState.ACTIVE).build();
        vet = Veterinarian.builder().id(20L).externalId(UUID.randomUUID()).active(false)
                .user(user).licenseNumber("LIC-1").phone("123")
                .branch(Branch.builder().id(30L).build()).workStartTime(LocalTime.of(9, 0))
                .workDays(Set.of(DayOfWeek.MONDAY)).build();
        credentials = Credentials.builder().id(40L).user(user).enabled(true)
                .password("hash").refreshToken("refresh").roles(Set.of(clientRole)).build();
    }

    private void found() {
        when(repository.findByExternalId(vet.getExternalId())).thenReturn(Optional.of(vet));
    }

    private void eligible() {
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        when(roleRepository.findByRole(Roles.ROLE_VETERINARIAN)).thenReturn(Optional.of(vetRole));
    }

    @Test
    void reactivateRestoresMissingRoleAndPreservesIdentityAndData() {
        eligible();
        VeterinarianResponse response = mock(VeterinarianResponse.class);
        when(repository.save(vet)).thenReturn(vet);
        when(mapper.toResponse(vet)).thenReturn(response);
        UUID id = vet.getExternalId();
        Branch branch = vet.getBranch();

        assertThat(service.reactivate(id)).isSameAs(response);
        assertThat(vet.getActive()).isTrue();
        assertThat(vet.getExternalId()).isEqualTo(id);
        assertThat(vet.getUser()).isSameAs(user);
        assertThat(vet.getBranch()).isSameAs(branch);
        assertThat(vet.getLicenseNumber()).isEqualTo("LIC-1");
        assertThat(vet.getWorkStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(vet.getWorkDays()).containsExactly(DayOfWeek.MONDAY);
        assertThat(credentials.getRoles()).containsExactlyInAnyOrder(clientRole, vetRole);
        assertThat(credentials.getRoles()).isInstanceOf(HashSet.class);
        assertThat(credentials.getPassword()).isEqualTo("hash");
        assertThat(credentials.getRefreshToken()).isEqualTo("refresh");
        assertThat(credentials.isEnabled()).isTrue();
        assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
        verify(credentialsRepository).save(credentials);
        verify(repository).save(vet);
    }

    @Test
    void reactivateWithExistingRoleDoesNotDuplicateOrSaveCredentials() {
        credentials.setRoles(Set.of(clientRole, vetRole));
        eligible();
        service.reactivate(vet.getExternalId());
        assertThat(credentials.getRoles()).hasSize(2);
        verify(credentialsRepository, never()).save(any());
        verify(repository).save(vet);
    }

    @Test
    void reactivateRejectsInactiveUserWithoutWrites() {
        user.setUserState(UserState.INACTIVE);
        found();
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("user is inactive");
        unchanged();
    }

    @Test
    void reactivateRejectsDisabledCredentialsWithoutWrites() {
        credentials.setEnabled(false);
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("credentials are disabled");
        unchanged();
    }

    @Test
    void reactivateRejectsMissingProfile() {
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class);
        unchanged();
    }

    @Test
    void reactivateRejectsAlreadyActiveProfile() {
        vet.setActive(true);
        found();
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("already active");
        verifyNoInteractions(credentialsRepository, roleRepository);
        verify(repository, never()).save(any());
    }

    @Test
    void reactivateRejectsMissingUserAndCredentials() {
        vet.setUser(null);
        found();
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no associated user");
        unchanged();

        vet.setUser(user);
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no associated credentials");
        unchanged();
    }

    @Test
    void reactivateRejectsMissingRoleWithoutWrites() {
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("ROLE_VETERINARIAN");
        unchanged();
    }

    @Test
    void reactivateRejectsMissingRolesCollection() {
        credentials.setRoles(null);
        eligible();
        assertThatThrownBy(() -> service.reactivate(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no roles collection");
        unchanged();
    }

    private void unchanged() {
        assertThat(vet.getActive()).isFalse();
        verify(repository, never()).save(any());
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void deleteRemovesOnlyProfessionalRoleAndRejectsSecondDelete() {
        vet.setActive(true);
        credentials.setRoles(Set.of(clientRole, vetRole));
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        service.delete(vet.getExternalId());
        assertThat(vet.getActive()).isFalse();
        assertThat(credentials.getRoles()).containsExactly(clientRole);
        assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
        assertThat(credentials.isEnabled()).isTrue();
        verify(credentialsRepository).save(credentials);
        assertThatThrownBy(() -> service.delete(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, times(1)).save(vet);
    }
}
