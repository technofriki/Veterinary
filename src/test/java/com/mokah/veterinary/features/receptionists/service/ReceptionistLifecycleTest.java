package com.mokah.veterinary.features.receptionists.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.mapper.ReceptionistMapper;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceptionistLifecycleTest {
    @Mock ReceptionistRepository repository;
    @Mock ReceptionistMapper mapper;
    @Mock CredentialsRepository credentialsRepository;
    @Mock RoleRepository roleRepository;
    @InjectMocks ReceptionistServiceImpl service;

    private final Role receptionistRole = Role.builder().id(1L).role(Roles.ROLE_RECEPTIONIST).build();
    private final Role clientRole = Role.builder().id(2L).role(Roles.ROLE_CLIENT).build();
    private User user;
    private Receptionist receptionist;
    private Credentials credentials;

    @BeforeEach
    void setup() {
        user = User.builder().id(10L).userState(UserState.ACTIVE).build();
        receptionist = Receptionist.builder().id(20L).externalId(UUID.randomUUID()).active(false)
                .user(user).phone("123").branch(Branch.builder().id(30L).build()).build();
        credentials = Credentials.builder().id(40L).user(user).enabled(true)
                .password("hash").refreshToken("refresh").roles(Set.of(clientRole)).build();
    }

    private void found() {
        when(repository.findByExternalId(receptionist.getExternalId())).thenReturn(Optional.of(receptionist));
    }

    private void eligible() {
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        when(roleRepository.findByRole(Roles.ROLE_RECEPTIONIST)).thenReturn(Optional.of(receptionistRole));
    }

    @Test
    void reactivateRestoresRoleAndPreservesIdentityAndData() {
        eligible();
        ReceptionistResponse response = mock(ReceptionistResponse.class);
        when(repository.save(receptionist)).thenReturn(receptionist);
        when(mapper.toResponse(receptionist)).thenReturn(response);
        UUID id = receptionist.getExternalId();
        Branch branch = receptionist.getBranch();

        assertThat(service.reactivate(id)).isSameAs(response);
        assertThat(receptionist.getActive()).isTrue();
        assertThat(receptionist.getExternalId()).isEqualTo(id);
        assertThat(receptionist.getUser()).isSameAs(user);
        assertThat(receptionist.getBranch()).isSameAs(branch);
        assertThat(receptionist.getPhone()).isEqualTo("123");
        assertThat(credentials.getRoles()).containsExactlyInAnyOrder(clientRole, receptionistRole);
        assertThat(credentials.getPassword()).isEqualTo("hash");
        assertThat(credentials.getRefreshToken()).isEqualTo("refresh");
        assertThat(credentials.isEnabled()).isTrue();
        verify(credentialsRepository).save(credentials);
        verify(repository).save(receptionist);
    }

    @Test
    void existingRoleIsNotDuplicated() {
        credentials.setRoles(Set.of(clientRole, receptionistRole));
        eligible();
        service.reactivate(receptionist.getExternalId());
        assertThat(credentials.getRoles()).hasSize(2);
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void inactiveUserCannotBeReactivated() {
        user.setUserState(UserState.INACTIVE);
        found();
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("user is inactive");
        unchanged();
    }

    @Test
    void disabledCredentialsCannotBeReactivated() {
        credentials.setEnabled(false);
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("credentials are disabled");
        unchanged();
    }

    @Test
    void missingProfileAndAlreadyActive() {
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class);
        unchanged();
        receptionist.setActive(true);
        found();
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("already active");
        verify(repository, never()).save(any());
    }

    @Test
    void missingUserCredentialsAndRole() {
        receptionist.setUser(null);
        found();
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no associated user");
        unchanged();

        receptionist.setUser(user);
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no associated credentials");
        unchanged();

        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("ROLE_RECEPTIONIST");
        unchanged();
    }

    @Test
    void missingRolesCollectionIsAnInconsistency() {
        credentials.setRoles(null);
        eligible();
        assertThatThrownBy(() -> service.reactivate(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("no roles collection");
        unchanged();
    }

    private void unchanged() {
        assertThat(receptionist.getActive()).isFalse();
        verify(repository, never()).save(any());
        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void deleteOnlyRemovesProfessionalRoleAndRejectsSecondDelete() {
        receptionist.setActive(true);
        credentials.setRoles(Set.of(clientRole, receptionistRole));
        found();
        when(credentialsRepository.findByUser(user)).thenReturn(Optional.of(credentials));
        service.delete(receptionist.getExternalId());
        assertThat(receptionist.getActive()).isFalse();
        assertThat(credentials.getRoles()).containsExactly(clientRole);
        assertThat(credentials.isEnabled()).isTrue();
        assertThat(user.getUserState()).isEqualTo(UserState.ACTIVE);
        verify(credentialsRepository).save(credentials);
        assertThatThrownBy(() -> service.delete(receptionist.getExternalId()))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, times(1)).save(receptionist);
    }
}
