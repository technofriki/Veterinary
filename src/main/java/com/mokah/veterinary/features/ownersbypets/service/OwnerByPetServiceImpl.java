package com.mokah.veterinary.features.ownersbypets.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.entity.Owner;
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

@Service
@RequiredArgsConstructor
public class OwnerByPetServiceImpl implements OwnerByPetService {

    private final OwnerByPetRepository repository;
    private final OwnerByPetMapper mapper;
    private final OwnerService ownerService;
    private final PetService petService;

    @Override
    public OwnerByPetResponse create(OwnerByPetDTO request) {

//        repository.existsByOwnerIdAndPetId
//        ES IGUAL A
//        SELECT EXISTS(
//                SELECT *
//                FROM owner_by_pet
//                WHERE owner_id = ?
//                AND pet_id = ?
//        )

        if (repository.existsByOwnerIdAndPetId(
                request.ownerId(),
                request.petId())) {

            throw new IllegalArgumentException( // crear exception personalizada
                    "This owner is already associated with this pet");
        }

        OwnerByPet entity = mapper.toEntity(request);

        entity.setOwner(ownerService.entityById(request.ownerId()));
        entity.setPet(petService.entityById(request.petId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public OwnerByPet entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner by Pet", id));
    }

    @Override
    public OwnerByPetResponse findById(Long id) {
        return mapper.toResponse(entityById(id));
    }

    @Override
    public List<OwnerByPetResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public OwnerByPetResponse update(Long id, OwnerByPetDTO request) {

        OwnerByPet entity = entityById(id);





//        repository.existsByOwnerIdAndPetIdAndIdNo
//        ES IGUAL A
//        SELECT EXISTS(
//                SELECT *
//                FROM owner_by_pet
//                WHERE owner_id = ?
//                AND pet_id = ?
//                AND id <> ?
//        )
        if (repository.existsByOwnerIdAndPetIdAndIdNot(

                request.ownerId(),
                request.petId(),
                id)) {

            throw new IllegalArgumentException(
                    "This owner is already associated with this pet");
        }

        entity.setOwner(ownerService.entityById(request.ownerId()));
        entity.setPet(petService.entityById(request.petId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        OwnerByPet entity = entityById(id);
        repository.delete(entity);
    }
}
