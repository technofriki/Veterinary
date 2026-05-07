package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.repository.BreedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreedService {

    private final BreedRepository breedRepository;
    private final BreedMapper breedMapper;

    public BreedService(BreedRepository breedRepository, BreedMapper breedMapper) {
        this.breedRepository = breedRepository;
        this.breedMapper = breedMapper;
    }

    public List<BreedResponse> findAll(){
        return breedRepository.findAll().stream()
                .map(breedMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BreedResponse findById (Long id){
        BreedEntity entity = breedRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Breed not found with id: "+id));
        return breedMapper.toResponse(entity);
    }

    public BreedResponse findByName (String name){
        BreedEntity entity = breedRepository.findAll().stream()
                .filter(n->n.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Breed name not found by name: "+name));
        return breedMapper.toResponse(entity);
    }

    public BreedResponse save (BreedRequest request){
        BreedEntity entity = breedMapper.toEntity(request);
        BreedEntity saved = breedRepository.save(entity);
        return breedMapper.toResponse(saved);
    }

    public void delete (Long id){
        BreedEntity entity = breedRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Breed not found with id: "+id));
        breedRepository.delete(entity);
    }

    public BreedResponse update (Long id, BreedRequest request){
        BreedEntity existingEntity = breedRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Breed not found with id: "+id));

        existingEntity.setName(request.getName());
        existingEntity.setColor(request.getColor());

        BreedEntity updated = breedRepository.save(existingEntity);
        return breedMapper.toResponse(updated);
    }
}
