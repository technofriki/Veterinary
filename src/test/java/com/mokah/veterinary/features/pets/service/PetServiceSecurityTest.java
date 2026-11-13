package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceSecurityTest {

    private static final String CLIENT_USERNAME = "client@veterinary.com";
    private static final String ADMIN_USERNAME = "admin";
    private static final String VETERINARIAN_USERNAME = "vet@veterinary.com";
    private static final String RECEPTIONIST_USERNAME = "receptionist@veterinary.com";

    @Mock
    private PetRepository repository;

    @Mock
    private PetMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerByPetRepository ownerByPetRepository;

    @Mock
    private AnimalTypeService animalTypeService;

    @Mock
    private BreedService breedService;

    @InjectMocks
    private PetServiceImpl service;

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

        when(userRepository.findByEmail(CLIENT_USERNAME)).thenReturn(Optional.of(user));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.of(owner));
        when(ownerByPetRepository.findByOwnerId(owner.getId())).thenReturn(links);
    }

    private Pet pet(UUID externalId) {
        return Pet.builder()
                .id(1L)
                .externalId(externalId)
                .name("Rex")
                .birthDate(LocalDate.of(2020, 1, 1))
                .active(true)
                .color("Brown")
                .build();
    }

    private PetRequest petRequest(UUID animalTypeId, UUID breedId) {
        return new PetRequest("Rex", LocalDate.of(2020, 1, 1), "Brown", animalTypeId, breedId);
    }

    private AnimalType animalType(UUID externalId) {
        return AnimalType.builder()
                .id(1L)
                .externalId(externalId)
                .name("Perro")
                .build();
    }

    private Breed breed(UUID externalId, AnimalType animalType) {
        return Breed.builder()
                .id(1L)
                .externalId(externalId)
                .name("Labrador")
                .animalType(animalType)
                .build();
    }

    // --- create tests for CLIENT ---

    @Test
    void clientCreatesPetAndAssociatesToOwnOwner() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId);
        Breed breed = breed(breedId, animalType);
        Pet savedPet = pet(UUID.randomUUID());

        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Owner owner = Owner.builder().id(10L).externalId(UUID.randomUUID()).user(user).build();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(userRepository.findByEmail(CLIENT_USERNAME)).thenReturn(Optional.of(user));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.of(owner));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(savedPet);
        when(mapper.toResponse(savedPet)).thenReturn(null);

        service.create(petRequest(animalTypeId, breedId));

        verify(ownerByPetRepository).save(argThat(ownerByPet ->
                ownerByPet.getOwner().equals(owner) && ownerByPet.getPet().equals(savedPet)
        ));
    }

    @Test
    void clientWithoutOwnerCannotCreatePet() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId);
        Breed breed = breed(breedId, animalType);

        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(userRepository.findByEmail(CLIENT_USERNAME)).thenReturn(Optional.of(user));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.empty());
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(pet(UUID.randomUUID()));

        assertThatThrownBy(() -> service.create(petRequest(animalTypeId, breedId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner");

        verify(ownerByPetRepository, never()).save(any());
    }

    @Test
    void adminCreatesPetWithoutOwnerAssociation() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId);
        Breed breed = breed(breedId, animalType);
        Pet savedPet = pet(UUID.randomUUID());

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(savedPet);
        when(mapper.toResponse(savedPet)).thenReturn(null);

        service.create(petRequest(animalTypeId, breedId));

        verify(ownerByPetRepository, never()).save(any());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void receptionistCreatesPetWithoutOwnerAssociation() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId);
        Breed breed = breed(breedId, animalType);
        Pet savedPet = pet(UUID.randomUUID());

        authenticateAs(RECEPTIONIST_USERNAME, "ROLE_RECEPTIONIST");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(savedPet);
        when(mapper.toResponse(savedPet)).thenReturn(null);

        service.create(petRequest(animalTypeId, breedId));

        verify(ownerByPetRepository, never()).save(any());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void clientCreatePetWithInvalidAnimalTypeThrowsException() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(animalTypeService.entityByExternalId(animalTypeId))
                .thenThrow(new ResourceNotFoundException("AnimalType", "externalId", animalTypeId));

        assertThatThrownBy(() -> service.create(petRequest(animalTypeId, breedId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("AnimalType");

        verify(repository, never()).save(any());
        verify(ownerByPetRepository, never()).save(any());
    }

    @Test
    void clientCreatePetWithBreedMismatchThrowsException() {
        UUID animalTypeId = UUID.randomUUID();
        UUID otherAnimalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId);
        AnimalType otherAnimalType = animalType(otherAnimalTypeId);
        Breed breed = breed(breedId, otherAnimalType);

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);

        assertThatThrownBy(() -> service.create(petRequest(animalTypeId, breedId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed does not match");

        verify(repository, never()).save(any());
        verify(ownerByPetRepository, never()).save(any());
    }

    // --- findAll tests ---

    @Test
    void clientFindAllReturnsOnlyOwnPets() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findAll()).thenReturn(Arrays.asList(ownPet, foreignPet));
        when(mapper.toResponse(any())).thenReturn(null);

        List<?> result = service.findAll();

        verify(mapper, times(1)).toResponse(ownPet);
        verify(mapper, never()).toResponse(foreignPet);
    }

    @Test
    void clientWithoutPetsFindAllReturnsEmpty() {
        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets();
        when(repository.findAll()).thenReturn(Arrays.asList(pet(UUID.randomUUID())));
        when(mapper.toResponse(any())).thenReturn(null);

        List<?> result = service.findAll();

        assertThat(result).isEmpty();
        verify(mapper, never()).toResponse(any());
    }

    @Test
    void adminFindAllReturnsAllPets() {
        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findAll()).thenReturn(Arrays.asList(pet(UUID.randomUUID())));
        when(mapper.toResponse(any())).thenReturn(null);

        service.findAll();

        verify(mapper).toResponse(any());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void veterinarianFindAllReturnsAllPets() {
        authenticateAs(VETERINARIAN_USERNAME, "ROLE_VETERINARIAN");
        when(repository.findAll()).thenReturn(Arrays.asList(pet(UUID.randomUUID())));
        when(mapper.toResponse(any())).thenReturn(null);

        service.findAll();

        verify(mapper).toResponse(any());
        verify(userRepository, never()).findByEmail(any());
    }

    // --- findByExternalId tests ---

    @Test
    void clientCanFindOwnPetByExternalId() {
        Pet ownPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(ownPet.getExternalId())).thenReturn(Optional.of(ownPet));
        when(mapper.toResponse(ownPet)).thenReturn(null);

        service.findByExternalId(ownPet.getExternalId());

        verify(mapper).toResponse(ownPet);
    }

    @Test
    void clientCannotFindAnotherClientsPetByExternalId() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByExternalId(foreignPet.getExternalId())).thenReturn(Optional.of(foreignPet));

        assertThatThrownBy(() -> service.findByExternalId(foreignPet.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet");

        verify(mapper, never()).toResponse(any());
    }

    @Test
    void adminCanFindAnyPetByExternalId() {
        Pet pet = pet(UUID.randomUUID());

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByExternalId(pet.getExternalId())).thenReturn(Optional.of(pet));
        when(mapper.toResponse(pet)).thenReturn(null);

        service.findByExternalId(pet.getExternalId());

        verify(mapper).toResponse(pet);
        verify(userRepository, never()).findByEmail(any());
    }

    // --- findByName tests ---

    @Test
    void clientCanFindOwnPetByName() {
        Pet ownPet = pet(UUID.randomUUID());

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByNameIgnoreCase(ownPet.getName())).thenReturn(Optional.of(ownPet));
        when(mapper.toResponse(ownPet)).thenReturn(null);

        service.findByName(ownPet.getName());

        verify(mapper).toResponse(ownPet);
    }

    @Test
    void clientCannotFindAnotherClientsPetByName() {
        Pet ownPet = pet(UUID.randomUUID());
        Pet foreignPet = pet(UUID.randomUUID());
        foreignPet.setName("OtherPet");

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        mockClientOwnsPets(ownPet);
        when(repository.findByNameIgnoreCase(foreignPet.getName())).thenReturn(Optional.of(foreignPet));

        assertThatThrownBy(() -> service.findByName(foreignPet.getName()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet");

        verify(mapper, never()).toResponse(any());
    }

    @Test
    void adminCanFindAnyPetByName() {
        Pet pet = pet(UUID.randomUUID());

        authenticateAs(ADMIN_USERNAME, "ROLE_ADMIN");
        when(repository.findByNameIgnoreCase(pet.getName())).thenReturn(Optional.of(pet));
        when(mapper.toResponse(pet)).thenReturn(null);

        service.findByName(pet.getName());

        verify(mapper).toResponse(pet);
        verify(userRepository, never()).findByEmail(any());
    }

    // --- Client without Owner association ---

    @Test
    void clientWithoutOwnerCannotAccessPet() {
        Pet pet = pet(UUID.randomUUID());
        User user = User.builder().id(1L).externalId(UUID.randomUUID()).build();
        Credentials credentials = Credentials.builder().username(CLIENT_USERNAME).user(user).build();

        authenticateAs(CLIENT_USERNAME, "ROLE_CLIENT");
        when(userRepository.findByEmail(CLIENT_USERNAME)).thenReturn(Optional.of(user));
        when(ownerRepository.findByUser(user)).thenReturn(Optional.empty());
        when(repository.findByExternalId(pet.getExternalId())).thenReturn(Optional.of(pet));

        assertThatThrownBy(() -> service.findByExternalId(pet.getExternalId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner");
    }
}
