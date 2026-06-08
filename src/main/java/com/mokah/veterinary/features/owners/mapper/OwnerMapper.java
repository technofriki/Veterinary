package com.mokah.veterinary.features.owners.mapper;

import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface OwnerMapper {

   Owner toEntity(OwnerRequest dto);

   OwnerResponse toResponse(Owner entity);

   List<OwnerResponse> toResponseList(List<Owner> entities);

   void updateEntity(@MappingTarget Owner entity, OwnerRequest dto);
}
