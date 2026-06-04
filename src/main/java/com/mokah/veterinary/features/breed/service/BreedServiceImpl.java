package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreedServiceImpl implements BreedService{

    private final BreedRepository breedRepository;
    private final BreedMapper breedMapper;

    @Override
    public BreedEntity entityById(Long id) {
        return breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed", id));
    }

    @Override
    public BreedResponse findById(Long id) {
        return breedMapper.toResponse(entityById(id));
    }

    @Override
    public List<BreedResponse> findAll() {
        return breedRepository.findAll()
                .stream()
                .map(breedMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BreedResponse create(BreedRequest request) {
        BreedEntity entity = breedMapper.toEntity(request);
        return breedMapper.toResponse(breedRepository.save(entity));
    }

    @Override
    public BreedResponse update(Long id, BreedRequest request) {
        BreedEntity entity = entityById(id);

        entity.setName(request.getName());
        entity.setColor(request.getColor());

        return breedMapper.toResponse(breedRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        BreedEntity entity = entityById(id);
        breedRepository.delete(entity);
    }
}