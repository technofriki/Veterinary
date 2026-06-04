package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.entity.Pet;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    @Override
    public Pet entityById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));
    }

    @Override
    public List<PetResponse> findAll() {
        return petMapper.toResponseList(petRepository.findAll());
    }

    @Override
    public PetResponse findById(Long id) {
        return petMapper.toResponse(entityById(id));
    }

    @Override
    public PetResponse findByName(String name) {
        Pet entity = petRepository.findAll().stream()
                .filter(n -> n.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found with name: " + name));
        return petMapper.toResponse(entity);
    }

    @Override
    public PetResponse create(PetRequest request) {
        Pet entity = petMapper.toEntity(request);
        return petMapper.toResponse(petRepository.save(entity));
    }

    @Override
    public PetResponse update(Long id, PetRequest request) {
        Pet entity = entityById(id);

        Pet mapperPet = petMapper.toEntity(request);
        entity.setName(mapperPet.getName());
        entity.setBirthDate(mapperPet.getBirthDate());
        entity.setAnimalType(mapperPet.getAnimalType());
        entity.setBreed(mapperPet.getBreed());

        Pet updated = petRepository.save(entity);
        return petMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Pet entity = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        petRepository.delete(entity);

    }
}
