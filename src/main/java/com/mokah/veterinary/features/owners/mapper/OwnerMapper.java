package com.mokah.veterinary.features.owners.mapper;

import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface OwnerMapper {

   Owner toEntity(OwnerRequest dto);

   @Mapping(source = "user.firstName", target = "firstName")
   @Mapping(source = "user.lastName", target = "lastName")
   @Mapping(source = "user.email", target = "email")
   OwnerResponse toResponse(Owner entity);

   List<OwnerResponse> toResponseList(List<Owner> entities);

   void updateEntity(@MappingTarget Owner entity, OwnerRequest dto);
}
