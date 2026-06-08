package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.model.Condition;
import com.mokah.veterinary.features.conditions.mapper.ConditionMapper;
import com.mokah.veterinary.features.conditions.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConditionServiceImpl implements ConditionService {

    private final ConditionRepository repository;
    private final ConditionMapper mapper;

    @Override
    public ConditionResponse create(ConditionRequest dto) {
        Condition entity = mapper.toEntity(dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<ConditionResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public Condition entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Condition", "externalId", externalId));
    }

    @Override
    public ConditionResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public ConditionResponse update(
            UUID externalId,
            ConditionRequest dto) {

        Condition entity = entityByExternalId(externalId);

        mapper.update(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}
