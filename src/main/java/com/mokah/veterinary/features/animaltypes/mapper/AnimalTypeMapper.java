package com.mokah.veterinary.features.animaltypes.mapper;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalType;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AnimalTypeMapper {


    AnimalType toEntity(AnimalTypeRequest request);


   AnimalTypeResponse toResponse(AnimalType entity);
}
