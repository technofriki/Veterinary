package com.mokah.veterinary.features.conditions.mapper;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.entity.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionMapper {

    ConditionResponse toResponse(Condition entity);

    List<ConditionResponse> toResponseList(List<Condition> entities);

    @Mapping(target = "id" , ignore = true)
    Condition toEntity(ConditionRequest request);

    @Mapping(target = "id", ignore = true)
    void updateCondition(@MappingTarget Condition entity, ConditionRequest request);
}
