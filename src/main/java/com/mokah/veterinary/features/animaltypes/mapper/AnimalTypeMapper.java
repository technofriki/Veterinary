package com.mokah.veterinary.features.animaltypes.mapper;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalTypeMapper {

    AnimalType toEntity(AnimalTypeRequest request);

    AnimalTypeResponse toResponse(AnimalType entity);
    List<AnimalTypeResponse> toResponseList(List<AnimalType> entities);
}
