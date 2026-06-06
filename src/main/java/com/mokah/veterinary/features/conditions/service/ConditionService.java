package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.entity.Condition;

import java.util.List;

public interface ConditionService {

    Condition entityById(Long id);

    ConditionResponse create(ConditionRequest dto);

    List<ConditionResponse> findAll();

    ConditionResponse findById(Long id);

    void delete(Long id);
}
