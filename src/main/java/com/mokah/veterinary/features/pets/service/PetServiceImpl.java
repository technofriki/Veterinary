package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository repository;
    private final PetMapper petMapper;
    private final AnimalTypeService animalTypeService;
    private final BreedService breedService;

    @Override
    public Pet entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));
    }

    @Override
    public List<PetResponse> findAll() {
        return petMapper.toResponseList(repository.findAll());
    }

    @Override
    public PetResponse findById(Long id) {
        return petMapper.toResponse(entityById(id));
    }

    @Override
    public PetResponse findByName(String name) {
        Pet entity = repository.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pet not found with " + name + ": " + name)
                );

        return petMapper.toResponse(entity);
    }

    @Override
    public PetResponse create(PetRequest request) {

        AnimalType animalType = animalTypeService.entityById(request.animalTypeId());
        BreedEntity breed = breedService.entityById(request.breedId());

        Pet entity = petMapper.toEntity(request);

        entity.setAnimalType(animalType);
        entity.setBreed(breed);

        return petMapper.toResponse(repository.save(entity));
    }

    @Override
    public PetResponse update(Long id, PetRequest request) {

        Pet entity = entityById(id);

        AnimalType animalType = animalTypeService.entityById(request.animalTypeId());
        BreedEntity breed = breedService.entityById(request.breedId());

        Pet mapped = petMapper.toEntity(request);

        entity.setName(mapped.getName());
        entity.setBirthDate(mapped.getBirthDate());
        entity.setAnimalType(animalType);
        entity.setBreed(breed);

        return petMapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Pet entity = entityById(id);
        repository.delete(entity);
    }
}