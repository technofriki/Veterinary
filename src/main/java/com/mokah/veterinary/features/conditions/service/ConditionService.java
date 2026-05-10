package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;

import java.util.List;

public interface ConditionService {

    ConditionResponse create(ConditionRequest request);

    List<ConditionResponse> findAll();

    ConditionResponse findById(Long id);

    void delete(Long id);
}
