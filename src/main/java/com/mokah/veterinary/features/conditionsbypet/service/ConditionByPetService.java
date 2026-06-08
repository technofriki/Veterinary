package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;

import java.util.List;
import java.util.UUID;

public interface ConditionByPetService {

    ConditionByPetResponse create(ConditionByPetDTO dto);

    ConditionByPet entityByExternalId(UUID externalId);

    ConditionByPetResponse findById(UUID externalId);

    List<ConditionByPetResponse> findAll();

    ConditionByPetResponse update(
            UUID externalId,
            ConditionByPetUpdateDTO dto
    );

    void deactivate(UUID externalId);
}
