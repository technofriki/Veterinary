package com.mokah.veterinary.features.breed.mapper;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.model.Breed;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BreedMapper {

    Breed toEntity(BreedRequest request);

    BreedResponse toResponse(Breed entity);
    List<BreedResponse> toResponseList(List<Breed> entities);
}
