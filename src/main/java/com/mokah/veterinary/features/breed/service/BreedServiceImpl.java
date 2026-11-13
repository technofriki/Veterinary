package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BreedServiceImpl implements BreedService {

    private final BreedRepository repository;
    private final BreedMapper mapper;
    private final AnimalTypeService animalTypeService;

    @Override
    public Breed entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Breed", "externalId", externalId));
    }

    @Override
    public BreedResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<BreedResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public BreedResponse create(BreedRequest dto) {
        String name = dto.name().trim();
        AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeId());
        if (repository.existsByNameIgnoreCaseAndAnimalType_ExternalId(name, animalType.getExternalId())) {
            throw new BusinessRuleException("Breed already exists for this AnimalType: " + name);
        }
        Breed entity = mapper.toEntity(dto);
        entity.setName(name);
        entity.setAnimalType(animalType);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public BreedResponse update(UUID externalId, BreedRequest dto) {

        Breed entity = entityByExternalId(externalId);

        String name = dto.name().trim();
        AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeId());

        if (!entity.getName().equalsIgnoreCase(name)
                || !entity.getAnimalType().getExternalId().equals(animalType.getExternalId())) {
            if (repository.existsByNameIgnoreCaseAndAnimalType_ExternalIdAndExternalIdNot(
                    name, animalType.getExternalId(), externalId)) {
                throw new BusinessRuleException("Breed already exists for this AnimalType: " + name);
            }
        }

        entity.setName(name);
        entity.setAnimalType(animalType);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }

    @Override
    public List<BreedResponse> findByAnimalType(UUID animalTypeId) {
        return mapper.toResponseList(repository.findByAnimalType_ExternalId(animalTypeId));
    }
}