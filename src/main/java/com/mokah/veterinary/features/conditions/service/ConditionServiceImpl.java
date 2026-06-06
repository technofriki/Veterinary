package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.entity.Condition;
import com.mokah.veterinary.features.conditions.mapper.ConditionMapper;
import com.mokah.veterinary.features.conditions.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionServiceImpl implements ConditionService {

    private final ConditionRepository repository;
    private final ConditionMapper mapper;

    @Override
    public Condition entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condition", id));
    }

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
    public ConditionResponse findById(Long id) {
        Condition entity = entityById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        Condition entity = entityById(id);
        repository.delete(entity);
    }
}
