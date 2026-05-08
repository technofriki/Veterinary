package com.mokah.veterinary.features.breed.mapper;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface BreedMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "breed")
@Mapping(source = "color", target = "breed")
BreedEntity toEntity (BreedRequest request);

@Mapping(source = "breed", target = "name")
@Mapping(source ="breed", target = "color")
BreedResponse toResponse (BreedEntity entity);
}
