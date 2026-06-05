package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.entity.Owner;
import com.mokah.veterinary.features.owners.mapper.OwnerMapper;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository repository;
    private final OwnerMapper ownerMapper;

    @Override
    public OwnerResponse create(OwnerRequest request) {

        if (repository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Owner with DNI " + request.getDni() + " already exists");
        }

        Owner owner = ownerMapper.toEntity(request);
        Owner savedOwner = repository.save(owner);
        return ownerMapper.toResponse(savedOwner);
    }

    @Override
    public List<OwnerResponse> findAll() {
        return ownerMapper.toResponseList(repository.findAll());
    }

    @Override
    public Owner entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", id));
    }

    @Override
    public OwnerResponse findById(Long id) {
        Owner owner = entityById(id);
        return ownerMapper.toResponse(owner);
    }

    @Override
    public OwnerResponse findByDni(String dni) {
        Owner owner = repository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "DNI", dni));
        return ownerMapper.toResponse(owner);
    }

    @Override
    public OwnerResponse update(Long id, OwnerRequest request) {
        Owner entity = entityById(id);

        if (!entity.getDni().equals(request.getDni())
                && repository.existsByDni(request.getDni())) {

            throw new IllegalArgumentException("Owner with DNI " + request.getDni() + " already exists");
        }

       ownerMapper.updateEntity(entity, request);

        Owner updatedOwner = repository.save(entity);

        return ownerMapper.toResponse(updatedOwner);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Owner", id);
        }

        repository.deleteById(id);
    }
}
