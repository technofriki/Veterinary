package com.mokah.veterinary.features.conditions.service;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.entity.Condition;

import java.util.List;
import java.util.UUID;

public interface ConditionService {

    ConditionResponse create(ConditionRequest dto);

    List<ConditionResponse> findAll();

    Condition entityByExternalId(UUID externalId);

    ConditionResponse findById(UUID externalId);

    ConditionResponse update(UUID externalId, ConditionRequest dto);

    void delete(UUID externalId);
}
