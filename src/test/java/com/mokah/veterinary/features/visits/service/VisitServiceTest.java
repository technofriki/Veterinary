package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.AppointmentNotConfirmedException;
import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.service.AppointmentService;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.dto.VisitUpdateDTO;
import com.mokah.veterinary.features.visits.dto.WalkInVisitRequest;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.model.Visit;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    private static final LocalDateTime APPOINTMENT_DATE = LocalDateTime.of(2030, 1, 15, 10, 0);

    @Mock
    private VisitRepository repository;

    @Mock
    private VisitMapper mapper;

    @Mock
    private VeterinarianService veterinarianService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PetService petService;

    @InjectMocks
    private VisitServiceImpl service;

    private Pet pet() {
        return Pet.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name("Rex")
                .active(true)
                .color("Brown")
                .build();
    }

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

    private Veterinarian veterinarian(User user, boolean active) {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(active)
                .user(user)
                .build();
    }

    private Appointment confirmedAppointment(Pet pet, Veterinarian veterinarian) {
        return Appointment.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .appointmentDate(APPOINTMENT_DATE)
                .durationMinutes(30)
                .reason("Checkup")
                .status(AppointmentStatus.CONFIRMED)
                .pet(pet)
                .veterinarian(veterinarian)
                .build();
    }

    private VisitRequest visitRequest(Appointment appointment, Veterinarian veterinarian) {
        return new VisitRequest(
                "Observations",
                appointment.getExternalId(),
                veterinarian.getExternalId()
        );
    }

    private WalkInVisitRequest walkInVisitRequest(Pet pet, Veterinarian veterinarian) {
        return new WalkInVisitRequest(
                "Observations",
                pet.getExternalId(),
                veterinarian.getExternalId()
        );
    }

    @Test
    void createWithInactiveVeterinarianShouldFail() {
        Pet pet = pet();
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian inactiveVet = veterinarian(vetUser, false);
        Appointment appointment = confirmedAppointment(pet, inactiveVet);

        when(appointmentService.entityByExternalId(appointment.getExternalId())).thenReturn(appointment);
        when(repository.existsByAppointment_ExternalId(appointment.getExternalId())).thenReturn(false);

        doThrow(new BusinessRuleException("Cannot assign an inactive veterinarian."))
                .when(veterinarianService).validateActive(inactiveVet.getExternalId());

        VisitRequest request = visitRequest(appointment, inactiveVet);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive veterinarian");

        verify(repository, never()).save(any());
    }

    @Test
    void createWithInactiveUserShouldFail() {
        Pet pet = pet();
        User inactiveVetUser = user(1L, UserState.INACTIVE);
        Veterinarian vet = veterinarian(inactiveVetUser, true);
        Appointment appointment = confirmedAppointment(pet, vet);

        when(appointmentService.entityByExternalId(appointment.getExternalId())).thenReturn(appointment);
        when(repository.existsByAppointment_ExternalId(appointment.getExternalId())).thenReturn(false);

        doThrow(new BusinessRuleException("Cannot assign a veterinarian with an inactive user account."))
                .when(veterinarianService).validateActive(vet.getExternalId());

        VisitRequest request = visitRequest(appointment, vet);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive user account");

        verify(repository, never()).save(any());
    }

    @Test
    void walkInCreateWithInactiveVeterinarianShouldFail() {
        Pet pet = pet();
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian inactiveVet = veterinarian(vetUser, false);

        doThrow(new BusinessRuleException("Cannot assign an inactive veterinarian."))
                .when(veterinarianService).validateActive(inactiveVet.getExternalId());

        WalkInVisitRequest request = walkInVisitRequest(pet, inactiveVet);

        assertThatThrownBy(() -> service.walkInCreate(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive veterinarian");

        verify(repository, never()).save(any());
    }

    @Test
    void walkInCreateWithInactiveUserShouldFail() {
        Pet pet = pet();
        User inactiveVetUser = user(1L, UserState.INACTIVE);
        Veterinarian vet = veterinarian(inactiveVetUser, true);

        doThrow(new BusinessRuleException("Cannot assign a veterinarian with an inactive user account."))
                .when(veterinarianService).validateActive(vet.getExternalId());

        WalkInVisitRequest request = walkInVisitRequest(pet, vet);

        assertThatThrownBy(() -> service.walkInCreate(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive user account");

        verify(repository, never()).save(any());
    }

    @Test
    void updateWithInactiveVeterinarianShouldFail() {
        Pet pet = pet();
        User activeVetUser = user(1L, UserState.ACTIVE);
        User inactiveVetUser = user(2L, UserState.ACTIVE);
        Veterinarian activeVet = veterinarian(activeVetUser, true);
        Veterinarian inactiveVet = veterinarian(inactiveVetUser, false);

        Visit visit = Visit.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .observations("Old observations")
                .visitDate(LocalDateTime.now())
                .pet(pet)
                .veterinarian(activeVet)
                .build();

        when(repository.findByExternalId(visit.getExternalId())).thenReturn(Optional.of(visit));

        doThrow(new BusinessRuleException("Cannot assign an inactive veterinarian."))
                .when(veterinarianService).validateActive(inactiveVet.getExternalId());

        VisitUpdateDTO updateDTO = new VisitUpdateDTO(
                "New observations",
                inactiveVet.getExternalId()
        );

        assertThatThrownBy(() -> service.update(visit.getExternalId(), updateDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive veterinarian");

        verify(repository, never()).save(any());
    }

    @Test
    void updateWithSameVeterinarianShouldNotValidateActive() {
        Pet pet = pet();
        User vetUser = user(1L, UserState.ACTIVE);
        Veterinarian vet = veterinarian(vetUser, true);

        Visit visit = Visit.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .observations("Old observations")
                .visitDate(LocalDateTime.now())
                .pet(pet)
                .veterinarian(vet)
                .build();

        when(repository.findByExternalId(visit.getExternalId())).thenReturn(Optional.of(visit));
        when(veterinarianService.entityByExternalId(vet.getExternalId())).thenReturn(vet);
        when(repository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Visit.class))).thenReturn(new VisitResponse(
                visit.getExternalId(),
                visit.getObservations(),
                null,
                null,
                null,
                visit.getVisitDate()
        ));

        VisitUpdateDTO updateDTO = new VisitUpdateDTO(
                "New observations",
                vet.getExternalId()
        );

        service.update(visit.getExternalId(), updateDTO);

        verify(repository).save(any(Visit.class));
    }
}
