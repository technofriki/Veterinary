package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeterinarianServiceImplTest {

    @Mock
    private VeterinarianRepository repository;

    @InjectMocks
    private VeterinarianServiceImpl service;

    private User user(Long id, UserState state) {
        return User.builder()
                .id(id)
                .externalId(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email("user" + id + "@veterinary.com")
                .userState(state)
                .build();
    }

    private Veterinarian veterinarian(User user, Boolean active) {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(active)
                .user(user)
                .build();
    }

    @Test
    void validateActive_withInactiveVeterinarian_throwsBusinessRuleException() {
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian inactiveVet = veterinarian(vetUser, false);

        when(repository.findByExternalId(inactiveVet.getExternalId()))
                .thenReturn(Optional.of(inactiveVet));

        assertThatThrownBy(() -> service.validateActive(inactiveVet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot assign an inactive veterinarian");
    }

    @Test
    void validateActive_withInactiveUser_throwsBusinessRuleException() {
        User inactiveVetUser = user(1L, UserState.INACTIVE);
        Veterinarian vet = veterinarian(inactiveVetUser, true);

        when(repository.findByExternalId(vet.getExternalId()))
                .thenReturn(Optional.of(vet));

        assertThatThrownBy(() -> service.validateActive(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot assign a veterinarian with an inactive user account");
    }

    @Test
    void validateActive_withBothActive_doesNotThrowException() {
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian activeVet = veterinarian(vetUser, true);

        when(repository.findByExternalId(activeVet.getExternalId()))
                .thenReturn(Optional.of(activeVet));

        assertThatCode(() -> service.validateActive(activeVet.getExternalId()))
                .doesNotThrowAnyException();
    }

    @Test
    void validateActive_withNullActive_throwsBusinessRuleException() {
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian vet = veterinarian(vetUser, null);

        when(repository.findByExternalId(vet.getExternalId()))
                .thenReturn(Optional.of(vet));

        assertThatThrownBy(() -> service.validateActive(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot assign an inactive veterinarian");
    }

    @Test
    void validateActive_withNullUser_throwsBusinessRuleException() {
        Veterinarian vet = Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(true)
                .user(null)
                .build();

        when(repository.findByExternalId(vet.getExternalId()))
                .thenReturn(Optional.of(vet));

        assertThatThrownBy(() -> service.validateActive(vet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot assign a veterinarian with an inactive user account");
    }

    @Test
    void validateActive_withNonExistentVeterinarian_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        when(repository.findByExternalId(nonExistentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateActive(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateActive_withBothInactiveStates_throwsBusinessRuleExceptionForVeterinarian() {
        User inactiveVetUser = user(1L, UserState.INACTIVE);
        Veterinarian inactiveVet = veterinarian(inactiveVetUser, false);

        when(repository.findByExternalId(inactiveVet.getExternalId()))
                .thenReturn(Optional.of(inactiveVet));

        assertThatThrownBy(() -> service.validateActive(inactiveVet.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot assign an inactive veterinarian");
    }
}
