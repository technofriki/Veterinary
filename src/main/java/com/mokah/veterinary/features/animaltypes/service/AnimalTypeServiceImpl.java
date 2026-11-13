package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.animaltypes.repository.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalTypeServiceImpl implements AnimalTypeService {

    private final AnimalTypeRepository repository;
    private final AnimalTypeMapper mapper;

    @Override
    public AnimalType entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("AnimalType", "externalId", externalId));
    }

    @Override
    public AnimalTypeResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<AnimalTypeResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public AnimalTypeResponse create(AnimalTypeRequest dto) {
        String name = dto.name().trim();
        if (repository.findByNameIgnoreCase(name).isPresent()) {
            throw new BusinessRuleException("AnimalType already exists with name: " + name);
        }
        AnimalType entity = mapper.toEntity(dto);
        entity.setName(name);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public AnimalTypeResponse update(UUID externalId, AnimalTypeRequest dto) {

        AnimalType entity = entityByExternalId(externalId);

        String name = dto.name().trim();
        if (!entity.getName().equalsIgnoreCase(name)
                && repository.existsByNameIgnoreCaseAndIdNot(name, entity.getId())) {
            throw new BusinessRuleException("AnimalType already exists with name: " + name);
        }

        entity.setName(name);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}