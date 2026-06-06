package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;

import java.util.List;

public interface ConditionByPetService {
    ConditionByPetResponse create(ConditionByPetDTO dto);
    ConditionByPet entityById(Long id);
    ConditionByPetResponse findById(Long id);
    List<ConditionByPetResponse> findAll();
    ConditionByPetResponse update(Long id, ConditionByPetUpdateDTO dto);
    void deactivate(Long id);
}
