package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.entity.Owner;
import com.mokah.veterinary.features.owners.mapper.OwnerMapper;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    public OwnerServiceImpl(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    @Override
    public OwnerResponse create(OwnerRequest request) {

        if (ownerRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("Owner with DNI " + request.getDni() + " already exists");
        }

        Owner owner = ownerMapper.toEntity(request);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.toResponse(savedOwner);
    }

    @Override
    public List<OwnerResponse> findAll() {
        return ownerMapper.toResponseList(ownerRepository.findAll());
    }

    @Override
    public OwnerResponse findById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", id));
        return ownerMapper.toResponse(owner);
    }

    @Override
    public OwnerResponse findByDni(String dni) {
        Owner owner = ownerRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "DNI", dni));
        return ownerMapper.toResponse(owner);
    }

    @Override
    public OwnerResponse update(Long id, OwnerRequest request) {
        Owner existingOwner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", id));

        if (!existingOwner.getDni().equals(request.getDni())
                && ownerRepository.existsByDni(request.getDni())) {

            throw new IllegalArgumentException("Owner with DNI " + request.getDni() + " already exists");
        }

        Owner mappedOwner = ownerMapper.toEntity(request);

        existingOwner.setDni(mappedOwner.getDni());
        existingOwner.setEmail(mappedOwner.getEmail());
        existingOwner.setPhone(mappedOwner.getPhone());
        existingOwner.setFirstName(mappedOwner.getFirstName());
        existingOwner.setLastName(mappedOwner.getLastName());
        existingOwner.setAddress(mappedOwner.getAddress());

        Owner updatedOwner = ownerRepository.save(existingOwner);

        return ownerMapper.toResponse(updatedOwner);
    }

    @Override
    public void delete(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner", id);
        }

        ownerRepository.deleteById(id);
    }
}
