package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.entity.Condition;
import com.mokah.veterinary.features.conditions.mapper.ConditionMapper;
import com.mokah.veterinary.features.conditions.repository.ConditionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ConditionServiceImpl implements ConditionService{

    private final ConditionRepository conditionRepository;
    private final ConditionMapper conditionMapper;

    public ConditionServiceImpl(ConditionRepository conditionRepository, ConditionMapper conditionMapper) {
        this.conditionRepository = conditionRepository;
        this.conditionMapper = conditionMapper;
    }

    @Override
    public ConditionResponse create(ConditionRequest request) {
        Condition condition = conditionMapper.toEntity(request);
        conditionRepository.save(condition);
        return conditionMapper.toResponse(condition);
    }

    @Override
    public List<ConditionResponse> findAll() {
        return conditionMapper.toResponseList(conditionRepository.findAll());
    }

    @Override
    public ConditionResponse findById(Long id) {
        Condition condition = conditionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Condition not found with id: " + id));
        return conditionMapper.toResponse(condition);
    }

    @Override
    public void delete(Long id) {
        if (!conditionRepository.existsById(id)){
            throw new NoSuchElementException("Condition not found with id: "+id);
        }
        conditionRepository.deleteById(id);
    }
}
