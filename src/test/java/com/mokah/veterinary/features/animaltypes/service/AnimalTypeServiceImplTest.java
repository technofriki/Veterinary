package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.animaltypes.repository.AnimalTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalTypeServiceImplTest {

    @Mock
    private AnimalTypeRepository repository;

    @Mock
    private AnimalTypeMapper mapper;

    @InjectMocks
    private AnimalTypeServiceImpl service;

    private AnimalType animalType(Long id, String name) {
        return AnimalType.builder()
                .id(id)
                .externalId(UUID.randomUUID())
                .name(name)
                .build();
    }

    @Test
    void create_withDuplicateName_throwsBusinessRuleException() {
        when(repository.findByNameIgnoreCase("Perro")).thenReturn(Optional.of(animalType(1L, "Perro")));

        assertThatThrownBy(() -> service.create(new AnimalTypeRequest("Perro")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("AnimalType already exists with name");
    }

    @Test
    void create_withDuplicateNameAfterTrim_throwsBusinessRuleException() {
        when(repository.findByNameIgnoreCase("Perro")).thenReturn(Optional.of(animalType(1L, "Perro")));

        assertThatThrownBy(() -> service.create(new AnimalTypeRequest("  Perro  ")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("AnimalType already exists with name");
    }

    @Test
    void create_withDuplicateNameIgnoreCase_throwsBusinessRuleException() {
        when(repository.findByNameIgnoreCase("perro")).thenReturn(Optional.of(animalType(1L, "Perro")));

        assertThatThrownBy(() -> service.create(new AnimalTypeRequest("perro")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("AnimalType already exists with name");
    }

    @Test
    void create_withUniqueName_succeeds() {
        when(repository.findByNameIgnoreCase("Gato")).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(animalType(2L, "Gato"));
        when(repository.save(any())).thenReturn(animalType(2L, "Gato"));
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.create(new AnimalTypeRequest("Gato")))
                .doesNotThrowAnyException();
    }

    @Test
    void update_sameName_sameEntity_doesNotThrow() {
        AnimalType entity = animalType(1L, "Perro");
        when(repository.findByExternalId(entity.getExternalId())).thenReturn(Optional.of(entity));

        assertThatCode(() -> service.update(entity.getExternalId(), new AnimalTypeRequest("Perro")))
                .doesNotThrowAnyException();
    }

    @Test
    void update_toExistingName_throwsBusinessRuleException() {
        AnimalType entity = animalType(1L, "Perro");
        when(repository.findByExternalId(entity.getExternalId())).thenReturn(Optional.of(entity));
        when(repository.existsByNameIgnoreCaseAndIdNot("Gato", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(entity.getExternalId(), new AnimalTypeRequest("Gato")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("AnimalType already exists with name");
    }

    @Test
    void update_withTrimmedDuplicate_throwsBusinessRuleException() {
        AnimalType entity = animalType(1L, "Perro");
        when(repository.findByExternalId(entity.getExternalId())).thenReturn(Optional.of(entity));
        when(repository.existsByNameIgnoreCaseAndIdNot("Gato", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(entity.getExternalId(), new AnimalTypeRequest("  Gato  ")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("AnimalType already exists with name");
    }

    @Test
    void update_toUniqueName_succeeds() {
        AnimalType entity = animalType(1L, "Perro");
        when(repository.findByExternalId(entity.getExternalId())).thenReturn(Optional.of(entity));
        when(repository.existsByNameIgnoreCaseAndIdNot("Cachorro", 1L)).thenReturn(false);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(null);

        assertThatCode(() -> service.update(entity.getExternalId(), new AnimalTypeRequest("Cachorro")))
                .doesNotThrowAnyException();
    }
}
