package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.repository.BreedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreedServiceImplTest {

    @Mock
    private BreedRepository repository;

    @Mock
    private BreedMapper mapper;

    @Mock
    private AnimalTypeService animalTypeService;

    @InjectMocks
    private BreedServiceImpl service;

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

    private BreedRequest breedRequest(String name, UUID animalTypeId) {
        return new BreedRequest(name, animalTypeId);
    }

    @Test
    void create_withDuplicateNameInSameAnimalType_throwsBusinessRuleException() {
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalId("Labrador", animalTypeId))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(breedRequest("Labrador", animalTypeId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed already exists for this AnimalType");
    }

    @Test
    void create_withSameNameDifferentAnimalType_succeeds() {
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Gato");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalId("Labrador", animalTypeId))
                .thenReturn(false);
        when(mapper.toEntity(any())).thenReturn(breed(UUID.randomUUID(), "Labrador", animalType));
        when(repository.save(any())).thenReturn(breed(UUID.randomUUID(), "Labrador", animalType));
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.create(breedRequest("Labrador", animalTypeId)))
                .doesNotThrowAnyException();
    }

    @Test
    void create_withUniqueName_succeeds() {
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalId("Golden Retriever", animalTypeId))
                .thenReturn(false);
        when(mapper.toEntity(any())).thenReturn(breed(UUID.randomUUID(), "Golden Retriever", animalType));
        when(repository.save(any())).thenReturn(breed(UUID.randomUUID(), "Golden Retriever", animalType));
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.create(breedRequest("Golden Retriever", animalTypeId)))
                .doesNotThrowAnyException();
    }

    @Test
    void update_sameValues_sameEntity_succeeds() {
        UUID externalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed entity = breed(externalId, "Labrador", animalType);

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.update(externalId, breedRequest("Labrador", animalTypeId)))
                .doesNotThrowAnyException();
    }

    @Test
    void update_toExistingNameInSameType_throwsBusinessRuleException() {
        UUID externalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");
        Breed entity = breed(externalId, "Labrador", animalType);

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalIdAndExternalIdNot(
                "Golden Retriever", animalTypeId, externalId)).thenReturn(true);

        assertThatThrownBy(() -> service.update(externalId, breedRequest("Golden Retriever", animalTypeId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed already exists for this AnimalType");
    }

    @Test
    void update_toExistingNameDifferentType_succeeds() {
        UUID externalId = UUID.randomUUID();
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Gato");
        AnimalType originalAnimalType = animalType(UUID.randomUUID(), "Perro");
        Breed entity = breed(externalId, "Labrador", originalAnimalType);

        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(entity));
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalIdAndExternalIdNot(
                "Labrador", animalTypeId, externalId)).thenReturn(false);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.update(externalId, breedRequest("Labrador", animalTypeId)))
                .doesNotThrowAnyException();
    }

    @Test
    void findByAnimalType_returnsList() {
        UUID animalTypeId = UUID.randomUUID();
        when(repository.findByAnimalType_ExternalId(animalTypeId)).thenReturn(List.of());
        when(mapper.toResponseList(any())).thenReturn(List.of());

        assertThatCode(() -> service.findByAnimalType(animalTypeId))
                .doesNotThrowAnyException();
    }

    @Test
    void create_withDuplicateNameAfterTrim_throwsBusinessRuleException() {
        UUID animalTypeId = UUID.randomUUID();
        AnimalType animalType = animalType(animalTypeId, "Perro");
        when(animalTypeService.entityByExternalId(animalTypeId)).thenReturn(animalType);
        when(repository.existsByNameIgnoreCaseAndAnimalType_ExternalId("Labrador", animalTypeId))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(breedRequest("  Labrador  ", animalTypeId)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Breed already exists for this AnimalType");
    }
}
