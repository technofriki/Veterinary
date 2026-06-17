package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.repository.BreedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BreedServiceImpl implements BreedService {

    private final BreedRepository repository;
    private final BreedMapper mapper;

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
    public BreedResponse create(BreedRequest request) {
        Breed entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public BreedResponse update(UUID externalId, BreedRequest request) {

        Breed entity = entityByExternalId(externalId);

        entity.setName(request.name());

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }

    @Override
    @Transactional
    public Breed findOrCreate(BreedRequest dto) {

        String name = dto.name().trim();

        return repository.findByNameIgnoreCase(name)
                .orElseGet(() -> repository.save(Breed.builder()
                                .name(name)
                                .build()
                ));
    }
}