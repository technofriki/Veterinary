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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    Condition toEntity(ConditionRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    void update(@MappingTarget Condition entity, ConditionRequest dto);
}
