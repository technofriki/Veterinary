package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.entity.Condition;
import com.mokah.veterinary.features.conditions.service.ConditionService;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.exception.ConditionByPetExistsException;
import com.mokah.veterinary.features.conditionsbypet.mapper.ConditionByPetMapper;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import com.mokah.veterinary.features.conditionsbypet.repository.ConditionByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionByPetServiceImpl implements ConditionByPetService {

    private final ConditionByPetRepository repository;
    private final ConditionByPetMapper mapper;
    private final ConditionService conditionService;
    private final PetService petService;

    @Override
    public ConditionByPetResponse create(ConditionByPetDTO dto) {
        ConditionByPet entity = mapper.toEntity(dto);

        if (repository.existsByConditionIdAndPetId(dto.conditionId(), dto.petId())){
            throw new ConditionByPetExistsException("Pet with that condition already exists");
        }

        Condition condition = conditionService.entityById(dto.conditionId());
        Pet pet = petService.entityById(dto.petId());

        entity.setCondition(condition);
        entity.setPet(pet);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public ConditionByPet entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condition by pet", id));
    }

    @Override
    public ConditionByPetResponse findById(Long id) {
        ConditionByPet entity = entityById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public List<ConditionByPetResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public ConditionByPetResponse update(Long id, ConditionByPetUpdateDTO dto) {
        ConditionByPet entity = entityById(id);

        mapper.update(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void deactivate(Long id) {
        ConditionByPet entity = entityById(id);
        entity.setActive(false);
        repository.save(entity);
    }
}
