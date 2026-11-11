package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceSecurityTest {

    private static final String CLIENT_USERNAME = "client@veterinary.com";
    private static final String ADMIN_USERNAME = "admin";
    private static final String RECEPTIONIST_USERNAME = "receptionist@veterinary.com";
    private static final String VETERINARIAN_USERNAME = "vet@veterinary.com";
    private static final LocalDateTime APPOINTMENT_DATE = LocalDateTime.of(2030, 1, 15, 10, 0);

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentMapper mapper;

    @Mock
    private PetService petService;

    @Mock
    private VeterinarianService veterinarianService;

    @Mock
    private BranchService branchService;

    @Mock
    private CredentialsRepository credentialsRepository;

    @Mock
    private ReceptionistRepository receptionistRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerByPetRepository ownerByPetRepository;

    @InjectMocks
    private AppointmentServiceImpl service;

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

    private void mockClientOwnsPets(Pet... pets) {
        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Credentials credentials = Credentials.builder().username(CLIENT_USERNAME).user(user).build();
        Owner owner = Owner.builder().id(10L).externalId(UUID.randomUUID()).user(user).build();
        List<OwnerByPet> links = Arrays.stream(pets)
                .map(pet -> OwnerByPet.builder().owner(owner).pet(pet).build())
                .collect(Collectors.toList());

        when(credentialsRepository.findByUsername(CLIENT_USERNAME)).thenReturn(Optional.of(credentials));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.of(owner));
        when(ownerByPetRepository.findByOwnerId(owner.getId())).thenReturn(links);
    }

    private Pet pet() {
        return Pet.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name("Rex")
                .active(true)
                .color("Brown")
                .build();
    }

    private Branch branch() {
        return branch(1L);
    }

    private Branch branch(Long id) {
        return Branch.builder()
                .id(id)
                .externalId(UUID.randomUUID())
                .name("Central")
                .phone("555-1000")
                .email("central@veterinary.com")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(18, 0))
                .build();
    }

    private Veterinarian veterinarian() {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(true)
                .workStartTime(LocalTime.of(9, 0))
                .workEndTime(LocalTime.of(17, 0))
                .build();
    }

    private Veterinarian veterinarian(User user, Branch branch, boolean active) {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(active)
                .workStartTime(LocalTime.of(9, 0))
                .workEndTime(LocalTime.of(17, 0))
                .branch(branch)
                .user(user)
                .build();
    }

    private Receptionist receptionist(User user, Branch branch, boolean active) {
        return Receptionist.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .phone("555-3000")
                .active(active)
                .branch(branch)
                .user(user)
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

    private void mockCurrentCredentials(String username, User user, boolean enabled) {
        Credentials credentials = Credentials.builder()
                .username(username)
                .enabled(enabled)
                .user(user)
                .build();

        when(credentialsRepository.findByUsername(username)).thenReturn(Optional.of(credentials));
    }

    private Appointment appointment(Pet pet, AppointmentStatus status) {
        return Appointment.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .appointmentDate(APPOINTMENT_DATE)
                .durationMinutes(30)
                .reason("Checkup")
                .status(status)
                .pet(pet)
                .build();
    }

    private Appointment appointment(
            Pet pet,
            Veterinarian veterinarian,
            Branch branch,
            AppointmentStatus status) {

        return Appointment.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .appointmentDate(APPOINTMENT_DATE)
                .durationMinutes(30)
                .reason("Checkup")
                .status(status)
                .pet(pet)
                .veterinarian(veterinarian)
                .branch(branch)
                .build();
    }

    private AppointmentUpdateDTO updateDTO(Appointment appointment, AppointmentStatus status) {
        return updateDTO(
                appointment,
                status,
                appointment.getVeterinarian().getExternalId(),
                appointment.getBranch().getExternalId()
        );
    }

    private AppointmentUpdateDTO updateDTO(
            Appointment appointment,
            AppointmentStatus status,
            UUID veterinarianExternalId,
            UUID branchExternalId) {

        return new AppointmentUpdateDTO(
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                status,
                appointment.getPet().getExternalId(),
                veterinarianExternalId,
                branchExternalId
        );
    }

    private AppointmentResponse mockSuccessfulUpdate(
            Appointment appointment,
            Pet pet,
            Veterinarian veterinarian,
            Branch branch) {

        AppointmentResponse response = new AppointmentResponse(
                appointment.getExternalId(),
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                appointment.getStatus(),
                null,
                null,
                null
        );

        when(branchService.entityByExternalId(branch.getExternalId())).thenReturn(branch);
        when(repository.findByVeterinarian_ExternalIdAndStatusIn(eq(veterinarian.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(repository.findByPet_ExternalIdAndStatusIn(eq(pet.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(veterinarianService.entityByExternalId(veterinarian.getExternalId())).thenReturn(veterinarian);
        when(petService.entityByExternalId(pet.getExternalId())).thenReturn(pet);
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Appointment.class))).thenReturn(response);

        return response;
    }

    private AppointmentCreateDTO createDTO(Pet pet, Veterinarian veterinarian, Branch branch) {
        return new AppointmentCreateDTO(
                APPOINTMENT_DATE,
                30,
                "Checkup",
                pet.getExternalId(),
                veterinarian.getExternalId(),
                branch.getExternalId()
        );
    }

    private AppointmentResponse mockSuccessfulCreate(Pet pet, Veterinarian veterinarian, Branch branch) {
        AppointmentResponse response = new AppointmentResponse(
                UUID.randomUUID(),
                APPOINTMENT_DATE,
                30,
                "Checkup",
                AppointmentStatus.PENDING,
                null,
                null,
                null
        );

        when(branchService.entityByExternalId(branch.getExternalId())).thenReturn(branch);
        when(petService.entityByExternalId(pet.getExternalId())).thenReturn(pet);
        when(veterinarianService.entityByExternalId(veterinarian.getExternalId())).thenReturn(veterinarian);
        when(repository.findByVeterinarian_ExternalIdAndStatusIn(eq(veterinarian.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(repository.findByPet_ExternalIdAndStatusIn(eq(pet.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(mapper.toEntity(any(AppointmentCreateDTO.class))).thenReturn(new Appointment());
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Appointment.class))).thenReturn(response);

        return response;
    }

    @Test
    void clientCreatesAppointmentForOwnPet() {
        Pet ownPet = pet();
        Veterinarian veterinarian = veterinarian();
        Branch branch = branch();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        AppointmentResponse expected = mockSuccessfulCreate(ownPet, veterinarian, branch);

        AppointmentResponse result = service.create(createDTO(ownPet, veterinarian, branch));

        assertThat(result).isSameAs(expected);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(captor.getValue().getPet()).isSameAs(ownPet);
    }

    @Test
    void clientCannotCreateAppointmentForAnotherClientsPet() {
        Pet ownPet = pet();
        Pet foreignPet = pet();
        Veterinarian veterinarian = veterinarian();
        Branch branch = branch();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);

        assertThatThrownBy(() -> service.create(createDTO(foreignPet, veterinarian, branch)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet")
                .hasMessageContaining(foreignPet.getExternalId().toString());

        verify(repository, never()).save(any());
        verify(branchService, never()).entityByExternalId(any());
    }

    @Test
    void clientWithoutOwnerCannotCreateAppointment() {
        Pet pet = pet();
        Veterinarian veterinarian = veterinarian();
        Branch branch = branch();
        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Credentials credentials = Credentials.builder().username(CLIENT_USERNAME).user(user).build();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(credentialsRepository.findByUsername(CLIENT_USERNAME)).thenReturn(Optional.of(credentials));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(createDTO(pet, veterinarian, branch)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner");

        verify(repository, never()).save(any());
    }

    @Test
    void adminCreatesAppointmentWithoutOwnershipRestriction() {
        Pet pet = pet();
        Veterinarian veterinarian = veterinarian();
        Branch branch = branch();

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        mockSuccessfulCreate(pet, veterinarian, branch);

        service.create(createDTO(pet, veterinarian, branch));

        verify(repository).save(any(Appointment.class));
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void receptionistCreatesAppointmentWithoutOwnershipRestriction() {
        Pet pet = pet();
        Veterinarian veterinarian = veterinarian();
        Branch branch = branch();

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockSuccessfulCreate(pet, veterinarian, branch);

        service.create(createDTO(pet, veterinarian, branch));

        verify(repository).save(any(Appointment.class));
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void clientCancelsOwnPendingAppointment() {
        Pet ownPet = pet();
        Appointment appointment = appointment(ownPet, AppointmentStatus.PENDING);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        service.delete(appointment.getExternalId());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(repository).save(appointment);
    }

    @Test
    void clientCancelsOwnConfirmedAppointment() {
        Pet ownPet = pet();
        Appointment appointment = appointment(ownPet, AppointmentStatus.CONFIRMED);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        service.delete(appointment.getExternalId());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(repository).save(appointment);
    }

    @Test
    void clientCannotCancelAnotherClientsPendingAppointment() {
        Pet ownPet = pet();
        Pet foreignPet = pet();
        Appointment appointment = appointment(foreignPet, AppointmentStatus.PENDING);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.delete(appointment.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment")
                .hasMessageContaining(appointment.getExternalId().toString());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void clientCannotCancelAnotherClientsCompletedAppointment() {
        Pet ownPet = pet();
        Pet foreignPet = pet();
        Appointment appointment = appointment(foreignPet, AppointmentStatus.COMPLETED);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.delete(appointment.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment");

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(repository, never()).save(any());
    }

    @Test
    void clientCannotCancelOwnCompletedAppointment() {
        Pet ownPet = pet();
        Appointment appointment = appointment(ownPet, AppointmentStatus.COMPLETED);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.delete(appointment.getExternalId()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMPLETED");

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_ADMIN", "ROLE_RECEPTIONIST"})
    void staffCancelsAnyPendingAppointmentWithoutOwnershipRestriction(String authority) {
        Pet pet = pet();
        Appointment appointment = appointment(pet, AppointmentStatus.PENDING);

        authenticateAs("staff", authority);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        service.delete(appointment.getExternalId());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(repository).save(appointment);
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void clientFindsOwnAppointment() {
        Pet ownPet = pet();
        Appointment appointment = appointment(ownPet, AppointmentStatus.PENDING);
        AppointmentResponse response = new AppointmentResponse(
                appointment.getExternalId(),
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                appointment.getStatus(),
                null,
                null,
                null
        );

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(mapper.toResponse(appointment)).thenReturn(response);

        assertThat(service.findById(appointment.getExternalId())).isSameAs(response);
    }

    @Test
    void clientCannotFindAnotherClientsAppointment() {
        Pet ownPet = pet();
        Pet foreignPet = pet();
        Appointment appointment = appointment(foreignPet, AppointmentStatus.PENDING);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.findById(appointment.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment");

        verify(mapper, never()).toResponse(any());
    }

    @Test
    void adminFindsAnyAppointmentWithoutOwnershipRestriction() {
        Pet pet = pet();
        Appointment appointment = appointment(pet, AppointmentStatus.PENDING);
        AppointmentResponse response = new AppointmentResponse(
                appointment.getExternalId(),
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                appointment.getStatus(),
                null,
                null,
                null
        );

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(mapper.toResponse(appointment)).thenReturn(response);

        assertThat(service.findById(appointment.getExternalId())).isSameAs(response);
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void clientFindAllFiltersByOwnPets() {
        Pet ownPet = pet();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<AppointmentResponse> result = service.findAll(null, null, null, null, null, null, null);

        assertThat(result).isEmpty();
        verify(ownerByPetRepository).findByOwnerId(10L);
        verify(repository).findAll(any(PredicateSpecification.class));
    }

    @Test
    void clientWithoutPetsFindAllStillUsesOwnershipFilter() {
        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets();
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<AppointmentResponse> result = service.findAll(null, null, null, null, null, null, null);

        assertThat(result).isEmpty();
        verify(ownerByPetRepository).findByOwnerId(10L);
    }

    @Test
    void adminFindAllDoesNotUseOwnershipFilter() {
        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<AppointmentResponse> result = service.findAll(null, null, null, null, null, null, null);

        assertThat(result).isEmpty();
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void findAllWithoutAuthenticationDoesNotUseOwnershipFilter() {
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<AppointmentResponse> result = service.findAll(null, null, null, null, null, null, null);

        assertThat(result).isEmpty();
        verify(credentialsRepository, never()).findByUsername(anyString());
        verify(ownerByPetRepository, never()).findByOwnerId(any());
    }

    @Test
    void assignedVeterinarianConfirmsPendingAppointment() {
        User vetUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(vetUser, branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        service.confirmAppointment(appointment.getExternalId());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        verify(repository).save(appointment);
    }

    @Test
    void unassignedVeterinarianCannotConfirmAppointment() {
        User vetUser = user(1L, UserState.ACTIVE);
        User otherVetUser = user(2L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian otherVeterinarian = veterinarian(otherVetUser, branch, true);
        Appointment appointment = appointment(pet(), otherVeterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistConfirmsAppointmentInOwnBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        service.confirmAppointment(appointment.getExternalId());

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        verify(repository).save(appointment);
    }

    @Test
    void receptionistCannotConfirmAppointmentInAnotherBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch receptionistBranch = branch(1L);
        Branch appointmentBranch = branch(2L);
        Receptionist receptionist = receptionist(receptionistUser, receptionistBranch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), appointmentBranch, true);
        Appointment appointment = appointment(pet(), veterinarian, appointmentBranch, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void inactiveUserCannotConfirmAppointment() {
        User vetUser = user(1L, UserState.INACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(vetUser, branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void disabledCredentialsCannotConfirmAppointment() {
        User vetUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(vetUser, branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, false);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void inactiveVeterinarianCannotConfirmAppointment() {
        User vetUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(vetUser, branch, false);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void inactiveReceptionistCannotConfirmAppointment() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, false);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_ADMIN", "ROLE_CLIENT"})
    void unauthorizedRoleCannotConfirmAppointment(String authority) {
        User user = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs("other-user", authority);
        mockCurrentCredentials("other-user", user, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void confirmWithoutAuthenticationIsRejected() {
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        SecurityContextHolder.clearContext();
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AuthenticationException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void confirmNonexistentAppointmentIsRejected() {
        UUID externalId = UUID.randomUUID();
        when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmAppointment(externalId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotConfirmAppointmentWithNullBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, null, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotConfirmAppointmentWhenAppointmentBranchIsNull() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, null, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void veterinarianCannotConfirmAppointmentWhenVeterinarianIsNull() {
        User vetUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Appointment appointment = appointment(pet(), null, branch, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = {"CONFIRMED", "CANCELLED", "COMPLETED"})
    void onlyPendingAppointmentsCanBeConfirmed(AppointmentStatus status) {
        User vetUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(vetUser, branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, status);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, vetUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.confirmAppointment(appointment.getExternalId()))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(appointment.getStatus()).isEqualTo(status);
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = {"CONFIRMED", "CANCELLED", "COMPLETED"})
    void updateCannotChangeAppointmentStatus(AppointmentStatus newStatus) {
        User user = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user, branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, newStatus);

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(branchService.entityByExternalId(branch.getExternalId())).thenReturn(branch);

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository, never()).save(any());
    }

    @Test
    void updateWithSameStatusKeepsStatusAndUpdatesAppointment() {
        User user = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user, branch, true);
        Pet pet = pet();
        Appointment appointment = appointment(pet, veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);
        AppointmentResponse response = new AppointmentResponse(
                appointment.getExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                dto.reason(),
                appointment.getStatus(),
                null,
                null,
                null
        );

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(branchService.entityByExternalId(branch.getExternalId())).thenReturn(branch);
        when(repository.findByVeterinarian_ExternalIdAndStatusIn(eq(veterinarian.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(repository.findByPet_ExternalIdAndStatusIn(eq(pet.getExternalId()), anyList()))
                .thenReturn(List.of());
        when(veterinarianService.entityByExternalId(veterinarian.getExternalId())).thenReturn(veterinarian);
        when(petService.entityByExternalId(pet.getExternalId())).thenReturn(pet);
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Appointment.class))).thenReturn(response);

        AppointmentResponse result = service.update(appointment.getExternalId(), dto);

        assertThat(result).isSameAs(response);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(repository).save(appointment);
    }

    @Test
    void receptionistUpdatesAppointmentInOwnBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Pet pet = pet();
        Appointment appointment = appointment(pet, veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);
        AppointmentResponse response = mockSuccessfulUpdate(appointment, pet, veterinarian, branch);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        AppointmentResponse result = service.update(appointment.getExternalId(), dto);

        assertThat(result).isSameAs(response);
        verify(repository).save(appointment);
    }

    @Test
    void receptionistCanAssignActiveVeterinarianFromOwnBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian currentVeterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Veterinarian newVeterinarian = veterinarian(user(3L, UserState.ACTIVE), branch, true);
        Pet pet = pet();
        Appointment appointment = appointment(pet, currentVeterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                newVeterinarian.getExternalId(),
                branch.getExternalId()
        );
        AppointmentResponse response = mockSuccessfulUpdate(appointment, pet, newVeterinarian, branch);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        AppointmentResponse result = service.update(appointment.getExternalId(), dto);

        assertThat(result).isSameAs(response);
        assertThat(appointment.getVeterinarian()).isSameAs(newVeterinarian);
        verify(repository).save(appointment);
    }

    @Test
    void receptionistCannotUpdateAppointmentFromAnotherBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch receptionistBranch = branch(1L);
        Branch appointmentBranch = branch(2L);
        Receptionist receptionist = receptionist(receptionistUser, receptionistBranch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), appointmentBranch, true);
        Pet pet = pet();
        Appointment appointment = appointment(pet, veterinarian, appointmentBranch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                veterinarian.getExternalId(),
                receptionistBranch.getExternalId()
        );

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getBranch()).isSameAs(appointmentBranch);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotMoveAppointmentToAnotherBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch(1L);
        Branch anotherBranch = branch(2L);
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Pet pet = pet();
        Appointment appointment = appointment(pet, veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                veterinarian.getExternalId(),
                anotherBranch.getExternalId()
        );

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getBranch()).isSameAs(branch);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotAssignInactiveVeterinarian() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian currentVeterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Veterinarian inactiveVeterinarian = veterinarian(user(3L, UserState.ACTIVE), branch, false);
        Pet pet = pet();
        Appointment appointment = appointment(pet, currentVeterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                inactiveVeterinarian.getExternalId(),
                branch.getExternalId()
        );

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(veterinarianService.entityByExternalId(inactiveVeterinarian.getExternalId()))
                .thenReturn(inactiveVeterinarian);

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getVeterinarian()).isSameAs(currentVeterinarian);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotAssignVeterinarianFromAnotherBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch receptionistBranch = branch(1L);
        Branch anotherBranch = branch(2L);
        Receptionist receptionist = receptionist(receptionistUser, receptionistBranch, true);
        Veterinarian currentVeterinarian = veterinarian(user(2L, UserState.ACTIVE), receptionistBranch, true);
        Veterinarian veterinarianFromAnotherBranch = veterinarian(user(3L, UserState.ACTIVE), anotherBranch, true);
        Pet pet = pet();
        Appointment appointment = appointment(
                pet,
                currentVeterinarian,
                receptionistBranch,
                AppointmentStatus.PENDING
        );
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                veterinarianFromAnotherBranch.getExternalId(),
                receptionistBranch.getExternalId()
        );

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));
        when(veterinarianService.entityByExternalId(veterinarianFromAnotherBranch.getExternalId()))
                .thenReturn(veterinarianFromAnotherBranch);

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(appointment.getVeterinarian()).isSameAs(currentVeterinarian);
        verify(repository, never()).save(any());
    }

    @Test
    void receptionistWithDisabledCredentialsCannotUpdateAppointment() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, false);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistWithInactiveUserCannotUpdateAppointment() {
        User receptionistUser = user(10L, UserState.INACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistWithInactiveProfileCannotUpdateAppointment() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, false);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistWithoutProfileCannotUpdateAppointment() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.empty());
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotUpdateAppointmentWithNullBranch() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, null, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void receptionistCannotUpdateAppointmentWhenAppointmentBranchIsNull() {
        User receptionistUser = user(10L, UserState.ACTIVE);
        Branch branch = branch();
        Receptionist receptionist = receptionist(receptionistUser, branch, true);
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, null, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                appointment.getAppointmentDate(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                AppointmentStatus.PENDING,
                pet().getExternalId(),
                veterinarian.getExternalId(),
                branch.getExternalId()
        );

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        mockCurrentCredentials(RECEPTIONIST_USERNAME, receptionistUser, true);
        when(receptionistRepository.findByUser(receptionistUser)).thenReturn(Optional.of(receptionist));
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void veterinarianCannotUpdateAppointment() {
        User veterinarianUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        mockCurrentCredentials(VETERINARIAN_USERNAME, veterinarianUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void clientCannotUpdateAppointment() {
        User clientUser = user(1L, UserState.ACTIVE);
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockCurrentCredentials(CLIENT_USERNAME, clientUser, true);
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void updateWithoutAuthenticationIsRejected() {
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(2L, UserState.ACTIVE), branch, true);
        Appointment appointment = appointment(pet(), veterinarian, branch, AppointmentStatus.PENDING);
        AppointmentUpdateDTO dto = updateDTO(appointment, AppointmentStatus.PENDING);

        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.update(appointment.getExternalId(), dto))
                .isInstanceOf(AuthenticationException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void adminCanChangeAppointmentBranchAndVeterinarian() {
        Branch currentBranch = branch(1L);
        Branch newBranch = branch(2L);
        Veterinarian currentVeterinarian = veterinarian(user(2L, UserState.ACTIVE), currentBranch, true);
        Veterinarian newVeterinarian = veterinarian(user(3L, UserState.ACTIVE), newBranch, true);
        Pet pet = pet();
        Appointment appointment = appointment(
                pet,
                currentVeterinarian,
                currentBranch,
                AppointmentStatus.PENDING
        );
        AppointmentUpdateDTO dto = updateDTO(
                appointment,
                AppointmentStatus.PENDING,
                newVeterinarian.getExternalId(),
                newBranch.getExternalId()
        );
        AppointmentResponse response = mockSuccessfulUpdate(appointment, pet, newVeterinarian, newBranch);

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(appointment.getExternalId())).thenReturn(Optional.of(appointment));

        AppointmentResponse result = service.update(appointment.getExternalId(), dto);

        assertThat(result).isSameAs(response);
        assertThat(appointment.getBranch()).isSameAs(newBranch);
        assertThat(appointment.getVeterinarian()).isSameAs(newVeterinarian);
        verify(repository).save(appointment);
    }
}
