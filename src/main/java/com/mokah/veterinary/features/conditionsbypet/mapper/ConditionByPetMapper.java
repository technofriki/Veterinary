package com.mokah.veterinary.features.conditionsbypet.mapper;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionByPetMapper {

    @Mapping(target = "condition", ignore = true)
    @Mapping(target = "pet", ignore = true)
    ConditionByPet toEntity(ConditionByPetDTO request);

    ConditionByPetResponse toResponse(ConditionByPet entity);
    List<ConditionByPetResponse> toResponseList(List<ConditionByPet> entities);
}
