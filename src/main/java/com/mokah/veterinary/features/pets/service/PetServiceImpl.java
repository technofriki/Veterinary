package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.InvalidDateException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository repository;
    private final PetMapper mapper;
    private final AnimalTypeService animalTypeService;
    private final BreedService breedService;

    @Override
    public PetResponse create(PetRequest dto) {

        if(dto.birthDate() != null && dto.birthDate().isAfter(LocalDate.now())){
            throw new InvalidDateException("Birth date can not be after now");
        }

        AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeExternalId());
        Breed breed = breedService.entityByExternalId(dto.breedExternalId());

        Pet entity = Pet.builder()
                .name(dto.name())
                .birthDate(dto.birthDate())
                .animalType(animalType)
                .breed(breed)
                .active(true)
                .build();

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<PetResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public Pet entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "externalId", externalId));
    }

    @Override
    public PetResponse findByExternalId(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public PetResponse findByName(String name) {
        Pet entity = repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "name", name));

        return mapper.toResponse(entity);
    }

    @Override
    public PetResponse update(UUID externalId, PetRequest dto) {

        Pet entity = entityByExternalId(externalId);
        if (entity.getBirthDate() == null && dto.birthDate() != null) {
            if (dto.birthDate().isAfter(LocalDate.now())) {
                throw new InvalidDateException("Birth date can not be after now");
            }
            entity.setBirthDate(dto.birthDate());
        }

            AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeExternalId());
            Breed breed = breedService.entityByExternalId(dto.breedExternalId());


            entity.setName(dto.name());
            entity.setAnimalType(animalType);
            entity.setBreed(breed);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        Pet pet = entityByExternalId(externalId);
        if(!pet.getActive()){
            throw new BusinessRuleException("The selected pet is no longer active in the system.");
        }
        pet.setActive(false);
        repository.save(pet);
    }
}