package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

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

    private Pet pet(UUID externalId, String name, AnimalType animalType, Breed breed) {
        return Pet.builder()
                .id(1L)
                .externalId(externalId)
                .name(name)
                .birthDate(LocalDate.of(2020, 1, 1))
                .active(true)
                .color("Negro")
                .animalType(animalType)
                .breed(breed)
                .build();
    }

    private PetRequest petRequest(String name, UUID animalTypeId, UUID breedId) {
        return new PetRequest(name, LocalDate.of(2020, 1, 1), "Negro", animalTypeId, breedId);
    }

    @Test
    void create_withNonExistentAnimalType_throwsResourceNotFoundException() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        when(animalTypeService.entityByExternalId(animalTypeId))
                .thenThrow(new ResourceNotFoundException("AnimalType", "externalId", animalTypeId));

        assertThatThrownBy(() -> service.create(petRequest("Rex", animalTypeId, breedId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("AnimalType");
    }

    @Test
    void create_withNonExistentBreed_throwsResourceNotFoundException() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");

        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId))
                .thenThrow(new ResourceNotFoundException("Breed", "externalId", breedId));

        assertThatThrownBy(() -> service.create(petRequest("Rex", animalTypeId, breedId)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Breed");
    }

    @Test
    void create_withBreedNotMatchingAnimalType_throwsBusinessRuleException() {
        UUID animalTypeId = UUID.randomUUID();
        UUID otherAnimalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        AnimalType otherAnimalType = animalType(otherAnimalTypeId, "Gato");
        Breed breed = breed(breedId, "Siamés", otherAnimalType);

        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);

        assertThatThrownBy(() -> service.create(petRequest("Rex", animalTypeId, breedId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed does not match the selected AnimalType");
    }

    @Test
    void create_withValidData_succeeds() {
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();
        UUID petExternalId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet savedPet = pet(petExternalId, "Rex", animalType, breed);

        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(savedPet);
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.create(petRequest("Rex", animalTypeId, breedId)))
                .doesNotThrowAnyException();
    }

    @Test
    void update_withBreedNotMatchingAnimalType_throwsBusinessRuleException() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID otherAnimalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        AnimalType otherAnimalType = animalType(otherAnimalTypeId, "Gato");
        Breed breed = breed(breedId, "Siamés", otherAnimalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed);

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);

        assertThatThrownBy(() -> service.update(petExternalId, petRequest("Rex", animalTypeId, breedId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed does not match the selected AnimalType");
    }

    @Test
    void update_withValidData_succeeds() {
        UUID petExternalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        UUID breedId = UUID.randomUUID();

        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed breed = breed(breedId, "Labrador", animalType);
        Pet entity = pet(petExternalId, "Rex", animalType, breed);
        Pet updatedPet = pet(petExternalId, "Rex", animalType, breed);

        when(repository.findByExternalId(petExternalId)).thenReturn(java.util.Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(breedService.entityByExternalId(breedId)).thenReturn(breed);
        when(repository.save(any())).thenReturn(updatedPet);
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.update(petExternalId, petRequest("Rex", animalTypeId, breedId)))
                .doesNotThrowAnyException();
    }
}
