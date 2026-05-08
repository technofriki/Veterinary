package com.mokah.veterinary.features.breed.mapper;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface BreedMapper {

    @Mapping(target = "id", ignore = true)
    BreedEntity toEntity(BreedRequest request);

    BreedResponse toResponse(BreedEntity entity);
}
