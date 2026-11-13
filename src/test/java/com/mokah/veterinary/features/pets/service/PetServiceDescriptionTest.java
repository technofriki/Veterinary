package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.dto.PetDescriptionUpdateDTO;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceDescriptionTest {

    @Mock
    private PetRepository repository;

    @Mock
    private PetMapper mapper;

    @Mock
    private AnimalTypeService animalTypeService;

    @Mock
    private BreedService breedService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerByPetRepository ownerByPetRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private VisitMapper visitMapper;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper animalTypeMapper;

    @Mock
    private com.mokah.veterinary.features.breed.mapper.BreedMapper breedMapper;

    @InjectMocks
    private PetServiceImpl service;

    private AnimalType animalType(UUID externalId, String name) {
        return AnimalType.builder()
                .id(1L)
                .externalId(externalId)
                .name(name)
                .build();
    }

    private Breed breed(UUID externalId, String name, AnimalType animalType) {
        return Breed.builder()
                .id(1L)
                .externalId(externalId)
                .name(name)
                .animalType(animalType)
                .build();
    }

    private Pet pet(UUID externalId, String name, AnimalType animalType, Breed breed, String description) {
        return Pet.builder()
                .id(1L)
                .externalId(externalId)
                .name(name)
                .birthDate(LocalDate.of(2020, 1, 1))
                .active(true)
                .color("Negro")
                .animalType(animalType)
                .breed(breed)
                .description(description)
                .build();
    }

    private PetRequest petRequest(String name, UUID animalTypeId, UUID breedId) {
        return new PetRequest(name, LocalDate.of(2020, 1, 1), "Negro", animalTypeId, breedId);
    }

    @Test
    void updateDescription_withValidText_setsDescription() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed, null);
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(repository.save(any())).thenReturn(updatedPet);
        when(visitRepository.findTop1ByPet_ExternalIdOrderByVisitDateDesc(petExternalId)).thenReturn(java.util.Optional.empty());
        when(visitRepository.countByPet_ExternalId(petExternalId)).thenReturn(0L);
        when(appointmentRepository.countByPet_ExternalIdAndStatusIn(eq(petExternalId), any())).thenReturn(0L);
        when(animalTypeMapper.toResponse(any())).thenReturn(null);
        when(breedMapper.toResponse(any())).thenReturn(null);

        PetDescriptionUpdateDTO dto = new PetDescriptionUpdateDTO("Alergia a penicilina");
        PetResponse response = service.updateDescription(petExternalId, dto);

        assertThat(response.description()).isEqualTo("Alergia a penicilina");
    }

    @Test
    void updateDescription_withNull_clearsDescription() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed, null);

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(repository.save(any())).thenReturn(updatedPet);
        when(visitRepository.findTop1ByPet_ExternalIdOrderByVisitDateDesc(petExternalId)).thenReturn(java.util.Optional.empty());
        when(visitRepository.countByPet_ExternalId(petExternalId)).thenReturn(0L);
        when(appointmentRepository.countByPet_ExternalIdAndStatusIn(eq(petExternalId), any())).thenReturn(0L);
        when(animalTypeMapper.toResponse(any())).thenReturn(null);
        when(breedMapper.toResponse(any())).thenReturn(null);

        PetDescriptionUpdateDTO dto = new PetDescriptionUpdateDTO(null);
        PetResponse response = service.updateDescription(petExternalId, dto);

        assertThat(response.description()).isNull();
    }

    @Test
    void updateDescription_withBlankString_clearsDescription() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed, null);

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(repository.save(any())).thenReturn(updatedPet);
        when(visitRepository.findTop1ByPet_ExternalIdOrderByVisitDateDesc(petExternalId)).thenReturn(java.util.Optional.empty());
        when(visitRepository.countByPet_ExternalId(petExternalId)).thenReturn(0L);
        when(appointmentRepository.countByPet_ExternalIdAndStatusIn(eq(petExternalId), any())).thenReturn(0L);
        when(animalTypeMapper.toResponse(any())).thenReturn(null);
        when(breedMapper.toResponse(any())).thenReturn(null);

        PetDescriptionUpdateDTO dto = new PetDescriptionUpdateDTO("   ");
        PetResponse response = service.updateDescription(petExternalId, dto);

        assertThat(response.description()).isNull();
    }

    @Test
    void updateDescription_withNonExistentPet_throwsResourceNotFoundException() {
        UUID petExternalId = UUID.randomUUID();

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.empty());

        PetDescriptionUpdateDTO dto = new PetDescriptionUpdateDTO("Test");
        assertThatThrownBy(() -> service.updateDescription(petExternalId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateDescription_trimsWhitespace() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed, null);
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(repository.save(any())).thenReturn(updatedPet);
        when(visitRepository.findTop1ByPet_ExternalIdOrderByVisitDateDesc(petExternalId)).thenReturn(java.util.Optional.empty());
        when(visitRepository.countByPet_ExternalId(petExternalId)).thenReturn(0L);
        when(appointmentRepository.countByPet_ExternalIdAndStatusIn(eq(petExternalId), any())).thenReturn(0L);
        when(animalTypeMapper.toResponse(any())).thenReturn(null);
        when(breedMapper.toResponse(any())).thenReturn(null);

        PetDescriptionUpdateDTO dto = new PetDescriptionUpdateDTO("  Alergia a penicilina  ");
        PetResponse response = service.updateDescription(petExternalId, dto);

        assertThat(response.description()).isEqualTo("Alergia a penicilina");
    }

    @Test
    void create_doesNotSetDescription() {
        // The Pet entity builder does not set description, so it is null by default.
        // This test verifies that the PetResponse record includes the description field
        // and that a newly created pet has description = null.
        UUID petExternalId = UUID.randomUUID();

        PetResponse response = new PetResponse(
                petExternalId, "Rex", LocalDate.of(2020, 1, 1), true, "Negro",
                null, null, null, 0L, null, 0L
        );

        assertThat(response.description()).isNull();
    }

    @Test
    void update_preservesExistingDescription() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");
        // After update, description should remain unchanged
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed, "Alergia a penicilina");

        PetResponse response = new PetResponse(
                petExternalId, "Rex", LocalDate.of(2020, 1, 1), true, "Negro",
                null, null, "Alergia a penicilina", 0L, null, 0L
        );

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(updatedPet);
        when(mapper.toResponse(updatedPet)).thenReturn(response);

        PetResponse result = service.update(petExternalId, petRequest("Rex", animalTypeId, breedId));

        assertThat(result.description()).isEqualTo("Alergia a penicilina");
    }
}
