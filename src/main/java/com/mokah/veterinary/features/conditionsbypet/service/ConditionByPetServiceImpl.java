package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.model.Condition;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConditionByPetServiceImpl implements ConditionByPetService {

    private final ConditionByPetRepository repository;
    private final ConditionByPetMapper mapper;
    private final ConditionService conditionService;
    private final PetService petService;

    @Override
    public ConditionByPetResponse create(ConditionByPetDTO dto) {
        Condition condition = conditionService.entityByExternalId(dto.conditionExternalId());

        Pet pet = petService.entityByExternalId(dto.petExternalId());

        if (repository.existsByConditionIdAndPetId(condition.getId(), pet.getId())) {
            throw new ConditionByPetExistsException(
                    "Pet with that condition already exists"
            );
        }

        ConditionByPet entity = mapper.toEntity(dto);

        entity.setCondition(condition);
        entity.setPet(pet);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public ConditionByPet entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("ConditionByPet", "externalId", externalId));
    }

    @Override
    public ConditionByPetResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<ConditionByPetResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public ConditionByPetResponse update(
            UUID externalId,
            ConditionByPetUpdateDTO dto) {

        ConditionByPet entity = entityByExternalId(externalId);

        mapper.update(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void deactivate(UUID externalId) {

        ConditionByPet entity = entityByExternalId(externalId);

        entity.setActive(false);

        repository.save(entity);
    }
}
