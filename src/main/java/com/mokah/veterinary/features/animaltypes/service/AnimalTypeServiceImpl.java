package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.animaltypes.repository.AnimalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalTypeServiceImpl implements AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final AnimalTypeMapper animalTypeMapper;

    @Override
    public AnimalType entityById(Long id) {
        return animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnimalType", id));
    }

    @Override
    public AnimalTypeResponse findById(Long id) {
        return animalTypeMapper.toResponse(entityById(id));
    }

    @Override
    public List<AnimalTypeResponse> findAll() {
        return animalTypeRepository.findAll()
                .stream()
                .map(animalTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AnimalTypeResponse create(AnimalTypeRequest request) {
        AnimalType entity = animalTypeMapper.toEntity(request);
        return animalTypeMapper.toResponse(animalTypeRepository.save(entity));
    }

    @Override
    public AnimalTypeResponse update(Long id, AnimalTypeRequest request) {
        AnimalType entity = entityById(id);

        entity.setName(request.getName());

        return animalTypeMapper.toResponse(animalTypeRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        AnimalType entity = entityById(id);
        animalTypeRepository.delete(entity);
    }
}