package com.mokah.veterinary.features.ownersbypets.mapper;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerByPetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "pet", ignore = true)
    OwnerByPet toEntity(OwnerByPetDTO dto);

    OwnerByPetResponse toResponse(OwnerByPet entity);

    List<OwnerByPetResponse> toResponseList(List<OwnerByPet> entities);
}
