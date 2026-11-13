package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.model.Condition;
import com.mokah.veterinary.features.conditions.service.ConditionService;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.exception.ConditionByPetExistsException;
import com.mokah.veterinary.features.conditionsbypet.mapper.ConditionByPetMapper;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionSeverity;
import com.mokah.veterinary.features.conditionsbypet.repository.ConditionByPetRepository;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConditionByPetServiceSecurityTest {

    private static final String CLIENT_USERNAME = "client@veterinary.com";

    @Mock
    private ConditionByPetRepository repository;

    @Mock
    private ConditionByPetMapper mapper;

    @Mock
    private ConditionService conditionService;

    @Mock
    private PetService petService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerByPetRepository ownerByPetRepository;

    @InjectMocks
    private ConditionByPetServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAsClient() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        CLIENT_USERNAME,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
                )
        );
    }

    private void authenticateAsNoClient() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "vet@veterinary.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_VETERINARIAN"))
                )
        );
    }

    private void authenticateAsClientWithVet() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "multirole@veterinary.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_CLIENT"),
                                new SimpleGrantedAuthority("ROLE_VETERINARIAN")
                        )
                )
        );
    }

    private Pet pet(UUID externalId) {
        return Pet.builder()
                .id(1L)
                .externalId(externalId)
                .name("Rex")
                .active(true)
                .color("Negro")
                .build();
    }

    private Condition condition(UUID externalId) {
        return Condition.builder()
                .id(1L)
                .externalId(externalId)
                .name("Diabetes")
                .build();
    }

    private ConditionByPet conditionByPet(UUID externalId, Pet pet, Condition condition) {
        return ConditionByPet.builder()
                .id(1L)
                .externalId(externalId)
                .pet(pet)
                .condition(condition)
                .diagnosisDate(LocalDateTime.now())
                .severity(ConditionSeverity.MODERATE)
                .observations("Test")
                .active(true)
                .build();
    }

    private void mockClientOwnsPets(Pet... pets) {
        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Owner owner = Owner.builder().id(10L).externalId(UUID.randomUUID()).user(user).build();
        List<OwnerByPet> links = Arrays.stream(pets)
                .map(pet -> OwnerByPet.builder().owner(owner).pet(pet).build())
                .collect(Collectors.toList());

        when(userRepository.findByEmail(CLIENT_USERNAME)).thenReturn(Optional.of(user));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.of(owner));
        when(ownerByPetRepository.findByOwnerId(owner.getId())).thenReturn(links);
    }

    // ==================== findByPetExternalId ====================

    @Test
    void findByPetExternalId_clientOwnsPet_returnsConditions() {
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        mockClientOwnsPets(pet);
        authenticateAsClient();

        when(petService.entityByExternalId(petExternalId)).thenReturn(pet);
        when(repository.findByPet_ExternalIdAndActiveTrue(petExternalId)).thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConditionByPetResponse> result = service.findByPetExternalId(petExternalId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPetExternalId_clientDoesNotOwnPet_throwsResourceNotFoundException() {
        UUID petExternalId = UUID.randomUUID();
        UUID otherPetExternalId = UUID.randomUUID();
        Pet otherPet = pet(otherPetExternalId);
        mockClientOwnsPets(otherPet);
        authenticateAsClient();

        when(petService.entityByExternalId(petExternalId)).thenReturn(pet(petExternalId));

        assertThatThrownBy(() -> service.findByPetExternalId(petExternalId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByPetExternalId_nonClient_returnsAllConditions() {
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        authenticateAsNoClient();

        when(petService.entityByExternalId(petExternalId)).thenReturn(pet);
        when(repository.findByPet_ExternalIdAndActiveTrue(petExternalId)).thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConditionByPetResponse> result = service.findByPetExternalId(petExternalId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPetExternalId_clientWithVetRole_returnsAllConditions() {
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        authenticateAsClientWithVet();

        when(petService.entityByExternalId(petExternalId)).thenReturn(pet);
        when(repository.findByPet_ExternalIdAndActiveTrue(petExternalId)).thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConditionByPetResponse> result = service.findByPetExternalId(petExternalId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPetExternalId_nonExistentPet_throwsResourceNotFoundException() {
        UUID petExternalId = UUID.randomUUID();
        authenticateAsNoClient();

        when(petService.entityByExternalId(petExternalId))
                .thenThrow(new ResourceNotFoundException("Pet", "externalId", petExternalId));

        assertThatThrownBy(() -> service.findByPetExternalId(petExternalId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ==================== findAll ====================

    @Test
    void findAll_client_returnsOnlyOwnPetConditions() {
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        mockClientOwnsPets(pet);
        authenticateAsClient();

        when(repository.findByPet_ExternalIdInAndActiveTrue(List.of(petExternalId)))
                .thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConditionByPetResponse> result = service.findAll();

        assertThat(result).isEmpty();
        verify(repository, never()).findAll();
    }

    @Test
    void findAll_nonClient_returnsAllConditions() {
        authenticateAsNoClient();

        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<ConditionByPetResponse> result = service.findAll();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    // ==================== findById ====================

    @Test
    void findById_clientOwnsPet_returnsCondition() {
        UUID externalId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        Condition condition = condition(UUID.randomUUID());
        ConditionByPet cbp = conditionByPet(externalId, pet, condition);

        mockClientOwnsPets(pet);
        authenticateAsClient();

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(cbp));
        when(mapper.toResponse(cbp)).thenReturn(null);

        service.findById(externalId);

        // Should not throw
    }

    @Test
    void findById_clientDoesNotOwnPet_throwsResourceNotFoundException() {
        UUID externalId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();
        UUID otherPetExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        Pet otherPet = pet(otherPetExternalId);
        Condition condition = condition(UUID.randomUUID());
        ConditionByPet cbp = conditionByPet(externalId, pet, condition);

        mockClientOwnsPets(otherPet);
        authenticateAsClient();

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(cbp));

        assertThatThrownBy(() -> service.findById(externalId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_nonClient_returnsCondition() {
        UUID externalId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();
        Pet pet = pet(petExternalId);
        Condition condition = condition(UUID.randomUUID());
        ConditionByPet cbp = conditionByPet(externalId, pet, condition);

        authenticateAsNoClient();

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(cbp));
        when(mapper.toResponse(cbp)).thenReturn(null);

        service.findById(externalId);

        // Should not throw
    }

    // ==================== create duplicates ====================

    @Test
    void create_duplicateCondition_throwsConditionByPetExistsException() {
        UUID conditionExternalId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();

        Condition condition = condition(conditionExternalId);
        Pet pet = pet(petExternalId);

        when(conditionService.entityByExternalId(conditionExternalId)).thenReturn(condition);
        when(petService.entityByExternalId(petExternalId)).thenReturn(pet);
        when(repository.existsByConditionIdAndPetId(condition.getId(), pet.getId())).thenReturn(true);

        ConditionByPetDTO dto = new ConditionByPetDTO(
                conditionExternalId, petExternalId, LocalDateTime.now(), ConditionSeverity.MODERATE, "Test"
        );

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ConditionByPetExistsException.class);
    }

    @Test
    void create_inactiveAssociationAlsoBlocksDuplicate() {
        // The existsByConditionIdAndPetId does not filter by active,
        // so an inactive association also blocks creating a new one.
        UUID conditionExternalId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();

        Condition condition = condition(conditionExternalId);
        Pet pet = pet(petExternalId);

        when(conditionService.entityByExternalId(conditionExternalId)).thenReturn(condition);
        when(petService.entityByExternalId(petExternalId)).thenReturn(pet);
        // existsByConditionIdAndPetId returns true even for inactive associations
        when(repository.existsByConditionIdAndPetId(condition.getId(), pet.getId())).thenReturn(true);

        ConditionByPetDTO dto = new ConditionByPetDTO(
                conditionExternalId, petExternalId, LocalDateTime.now(), ConditionSeverity.MODERATE, "Test"
        );

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ConditionByPetExistsException.class);
    }
}
