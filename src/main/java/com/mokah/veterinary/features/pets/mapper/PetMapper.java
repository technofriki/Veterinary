package com.mokah.veterinary.features.pets.mapper;

import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.entity.PetEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PetMapper.class, BreedMapper.class})
public interface PetMapper {
    PetEntity toEntity (PetRequest request);

    PetResponse toResponse (PetEntity entity);

}
