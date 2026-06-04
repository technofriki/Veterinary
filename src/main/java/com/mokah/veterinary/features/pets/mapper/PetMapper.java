package com.mokah.veterinary.features.pets.mapper;

import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.model.Pet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AnimalTypeMapper.class, BreedMapper.class})
public interface PetMapper {
    Pet toEntity (PetRequest request);

    PetResponse toResponse (Pet entity);
    List<PetResponse> toResponseList (List<Pet> entities);

}
