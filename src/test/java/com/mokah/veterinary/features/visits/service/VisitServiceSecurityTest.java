package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.service.AppointmentService;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.prescriptions.mapper.PrescriptionMapper;
import com.mokah.veterinary.features.prescriptions.repository.PrescriptionRepository;
import com.mokah.veterinary.features.studies.mapper.StudyMapper;
import com.mokah.veterinary.features.studiesbyvisit.repository.StudyByVisitRepository;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.model.Visit;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
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
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceSecurityTest {

    private static final String CLIENT_USERNAME = "client@veterinary.com";
    private static final String ADMIN_USERNAME = "admin";
    private static final String VETERINARIAN_USERNAME = "vet@veterinary.com";
    private static final String RECEPTIONIST_USERNAME = "receptionist@veterinary.com";

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

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private StudyByVisitRepository studyByVisitRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private DiagnosisMapper diagnosisMapper;

    @Mock
    private StudyMapper studyMapper;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @Mock
    private CredentialsRepository credentialsRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerByPetRepository ownerByPetRepository;

    @InjectMocks
    private VisitServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // --- Helpers ---

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

    private Pet pet(UUID externalId) {
        return Pet.builder()
                .id(1L)
                .externalId(externalId)
                .name("Rex")
                .active(true)
                .color("Brown")
                .build();
    }

    private Visit visit(UUID visitId, Pet pet) {
        return Visit.builder()
                .id(1L)
                .externalId(visitId)
                .observations("Checkup")
                .visitDate(LocalDateTime.now())
                .pet(pet)
                .build();
    }

    private VisitResponse visitResponse(Visit visit) {
        return new VisitResponse(
                visit.getExternalId(),
                visit.getObservations(),
                null,
                null,
                null,
                visit.getVisitDate()
        );
    }

    // --- findById tests ---

    @Test
    void clientCanFindVisitByIdForOwnPet() {
        Pet ownPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, ownPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(mapper.toResponse(visit)).thenReturn(visitResponse(visit));

        VisitResponse result = service.findById(visitId);

        assertThat(result).isNotNull();
        assertThat(result.externalId()).isEqualTo(visitId);
    }

    @Test
    void clientCannotFindVisitByIdForAnotherClientsPet() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, foreignPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.findById(visitId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("do not have access");
    }

    @Test
    void adminCanFindVisitByIdWithoutRestriction() {
        Pet pet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, pet);

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(mapper.toResponse(visit)).thenReturn(visitResponse(visit));

        VisitResponse result = service.findById(visitId);

        assertThat(result).isNotNull();
        assertThat(result.externalId()).isEqualTo(visitId);
        verify(credentialsRepository, never()).findByUsername(any());
    }

    @Test
    void veterinarianCanFindVisitByIdWithoutRestriction() {
        Pet pet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, pet);

        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(mapper.toResponse(visit)).thenReturn(visitResponse(visit));

        VisitResponse result = service.findById(visitId);

        assertThat(result).isNotNull();
        verify(credentialsRepository, never()).findByUsername(any());
    }

    @Test
    void receptionistCanFindVisitByIdWithoutRestriction() {
        Pet pet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, pet);

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(mapper.toResponse(visit)).thenReturn(visitResponse(visit));

        VisitResponse result = service.findById(visitId);

        assertThat(result).isNotNull();
        verify(credentialsRepository, never()).findByUsername(any());
    }

    // --- findAll tests ---

    @Test
    @SuppressWarnings("unchecked")
    void clientFindAllReturnsOnlyOwnPetsVisits() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<VisitResponse> result = service.findAll(null, null, null, null);

        assertThat(result).isEmpty();
        verify(ownerByPetRepository).findByOwnerId(10L);
        verify(repository).findAll(any(PredicateSpecification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void adminFindAllReturnsAllVisits() {
        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        service.findAll(null, null, null, null);

        verify(repository).findAll(any(PredicateSpecification.class));
        verify(credentialsRepository, never()).findByUsername(any());
    }

    // --- findMedicalHistory tests ---

    @Test
    @SuppressWarnings("unchecked")
    void clientCanFindMedicalHistoryForOwnPet() {
        Pet ownPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        lenient().when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        List<VisitResponse> result = service.findMedicalHistory(ownPet.getExternalId());

        assertThat(result).isEmpty();
        verify(ownerByPetRepository).findByOwnerId(10L);
    }

    @Test
    void clientCannotFindMedicalHistoryForAnotherClientsPet() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);

        assertThatThrownBy(() -> service.findMedicalHistory(foreignPet.getExternalId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("do not have access");
    }

    @Test
    @SuppressWarnings("unchecked")
    void adminCanFindMedicalHistoryWithoutRestriction() {
        Pet pet = pet(UUID.randomUUID());

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        lenient().when(repository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        when(mapper.toResponseList(anyList())).thenReturn(List.of());

        service.findMedicalHistory(pet.getExternalId());

        verify(credentialsRepository, never()).findByUsername(any());
    }

    // --- findDiagnosesByVisit tests ---

    @Test
    void clientCanFindDiagnosesForOwnVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, ownPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(diagnosisRepository.findByVisit_ExternalId(visitId)).thenReturn(List.of());
        when(diagnosisMapper.toResponseList(any())).thenReturn(List.of());

        service.findDiagnosesByVisit(visitId);

        verify(diagnosisRepository).findByVisit_ExternalId(visitId);
    }

    @Test
    void clientCannotFindDiagnosesForAnotherClientsVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, foreignPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.findDiagnosesByVisit(visitId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("do not have access");
    }

    // --- findPrescriptionsByVisit tests ---

    @Test
    void clientCanFindPrescriptionsForOwnVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, ownPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(prescriptionRepository.findByDiagnosis_Visit_ExternalId(visitId)).thenReturn(List.of());
        when(prescriptionMapper.toResponseList(any())).thenReturn(List.of());

        service.findPrescriptionsByVisit(visitId);

        verify(prescriptionRepository).findByDiagnosis_Visit_ExternalId(visitId);
    }

    @Test
    void clientCannotFindPrescriptionsForAnotherClientsVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, foreignPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.findPrescriptionsByVisit(visitId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("do not have access");
    }

    // --- findStudiesByVisit tests ---

    @Test
    void clientCanFindStudiesForOwnVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, ownPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));
        when(studyByVisitRepository.findByVisit_ExternalId(visitId)).thenReturn(List.of());
        when(studyMapper.toResponseList(any())).thenReturn(List.of());

        service.findStudiesByVisit(visitId);

        verify(studyByVisitRepository).findByVisit_ExternalId(visitId);
    }

    @Test
    void clientCannotFindStudiesForAnotherClientsVisit() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, foreignPet);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.findStudiesByVisit(visitId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("do not have access");
    }

    // --- Client without Owner association ---

    @Test
    void clientWithoutOwnerCannotAccessVisit() {
        Pet pet = pet(UUID.randomUUID());
        UUID visitId = UUID.randomUUID();
        Visit visit = visit(visitId, pet);
        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Credentials credentials = Credentials.builder().username(CLIENT_USERNAME).user(user).build();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(credentialsRepository.findByUsername(CLIENT_USERNAME)).thenReturn(Optional.of(credentials));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.empty());
        when(repository.findByExternalId(visitId)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.findById(visitId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner");
    }
}
