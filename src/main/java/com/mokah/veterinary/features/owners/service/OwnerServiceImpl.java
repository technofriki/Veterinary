package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.mapper.OwnerMapper;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository repository;
    private final OwnerMapper mapper;

    @Override
    public OwnerResponse create(OwnerRequest dto) {

        if (repository.existsByDni(dto.dni())) {
            throw new IllegalArgumentException("Owner with DNI " + dto.dni() + " already exists");
        }

        Owner owner = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(owner));
    }

    @Override
    public Owner entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "externalId", externalId));
    }

    @Override
    public OwnerResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<OwnerResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public OwnerResponse findByDni(String dni) {
        Owner owner = repository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "dni", dni));

        return mapper.toResponse(owner);
    }

    @Override
    public OwnerResponse update(UUID externalId, OwnerRequest dto) {

        Owner entity = entityByExternalId(externalId);

        if (!entity.getDni().equals(dto.dni())
                && repository.existsByDni(dto.dni())) {
            throw new IllegalArgumentException("Owner with DNI already exists");
        }

        mapper.updateEntity(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}