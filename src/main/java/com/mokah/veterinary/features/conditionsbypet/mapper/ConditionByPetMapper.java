package com.mokah.veterinary.features.conditionsbypet.mapper;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionByPetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "condition", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "active", ignore = true)
    ConditionByPet toEntity(ConditionByPetDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "condition", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "active", ignore = true)
    void update(
            @MappingTarget ConditionByPet entity,
            ConditionByPetUpdateDTO dto
    );

    ConditionByPetResponse toResponse(
            ConditionByPet entity
    );

    List<ConditionByPetResponse> toResponseList(
            List<ConditionByPet> entities
    );
}
