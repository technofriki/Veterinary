package com.mokah.veterinary.features.animaltypes.mapper;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface AnimalTypeMapper {

   @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "animalType")
    AnimalTypeEntity toEntity(AnimalTypeRequest request);

   @Mapping(source = "animalType", target = "name")
   AnimalTypeResponse toResponse(AnimalTypeEntity entity);
}
