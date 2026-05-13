package com.mokah.veterinary.features.owners.mapper;

import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.entity.Owner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface OwnerMapper {

   Owner toEntity (OwnerRequest request);

   OwnerResponse toResponse (Owner entity);

   List<OwnerResponse> toResponseList(List<Owner> entities);
}
