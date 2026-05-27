package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.animaltypes.repository.AnimalTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final AnimalTypeMapper animalTypeMapper;

    public AnimalTypeService(AnimalTypeRepository animalTypeRepository, AnimalTypeMapper animalTypeMapper) {
        this.animalTypeRepository = animalTypeRepository;
        this.animalTypeMapper = animalTypeMapper;
    }

    public List<AnimalTypeResponse> findAll() {
        return animalTypeRepository.findAll().stream()
                .map(animalTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AnimalTypeResponse findById(Long id) {
        AnimalTypeEntity entity = animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnimalType", id));
        return animalTypeMapper.toResponse(entity);
    }

    public AnimalTypeResponse findByName(String name) {
        AnimalTypeEntity entity = animalTypeRepository.findAll().stream()
                .filter(at -> at.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AnimalType", "name", name));
        return animalTypeMapper.toResponse(entity);
    }

    public AnimalTypeResponse save(AnimalTypeRequest request) {
        AnimalTypeEntity entity = animalTypeMapper.toEntity(request);
        AnimalTypeEntity savedAnimalType = animalTypeRepository.save(entity); /// Aca creamos otro entity guardado para que al retornalo muestre el id
        return animalTypeMapper.toResponse(savedAnimalType);
    }

    public void delete(Long id) {
        AnimalTypeEntity entity = animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnimalType", id));
        animalTypeRepository.delete(entity);
        ///No se necesita mensaje porque el codigo lo mandaria. Eso se encargara el controller
    }

    public AnimalTypeResponse update(Long id, AnimalTypeRequest request) {
        AnimalTypeEntity existingEntity = animalTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnimalType", id));

        existingEntity.setName(request.getName());

        AnimalTypeEntity updated = animalTypeRepository.save(existingEntity); ///JPARepository cuando queres guardar un entity que ya tiene el mismo id que uno existente lo reemplaza
        return animalTypeMapper.toResponse(updated);
    }

}
