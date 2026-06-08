package com.mokah.veterinary.features.ownersbypets.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.service.OwnerService;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.mapper.OwnerByPetMapper;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerByPetServiceImpl implements OwnerByPetService {

    private final OwnerByPetRepository repository;
    private final OwnerByPetMapper mapper;
    private final OwnerService ownerService;
    private final PetService petService;

    @Override
    public OwnerByPetResponse create(OwnerByPetDTO dto) {

        Owner owner = ownerService.entityByExternalId(dto.ownerExternalId());
        Pet pet = petService.entityByExternalId(dto.petExternalId());

        if (repository.existsByOwnerIdAndPetId(owner.getId(), pet.getId())) {
            throw new IllegalArgumentException(
                    "This owner is already associated with this pet"
            );
        }

        OwnerByPet entity = mapper.toEntity(dto);

        entity.setOwner(owner);
        entity.setPet(pet);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public OwnerByPet entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("OwnerByPet", "externalId", externalId));
    }

    @Override
    public OwnerByPetResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<OwnerByPetResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public OwnerByPetResponse update(UUID externalId, OwnerByPetDTO dto) {

        OwnerByPet entity = entityByExternalId(externalId);

        Owner owner = ownerService.entityByExternalId(dto.ownerExternalId());
        Pet pet = petService.entityByExternalId(dto.petExternalId());

        if (repository.existsByOwnerIdAndPetIdAndIdNot(
                owner.getId(),
                pet.getId(),
                entity.getId())) {

            throw new IllegalArgumentException(
                    "This owner is already associated with this pet"
            );
        }

        entity.setOwner(owner);
        entity.setPet(pet);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}