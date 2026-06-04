package com.mokah.veterinary.features.adresses.mapper;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import com.mokah.veterinary.features.adresses.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address entity);

    void update(@MappingTarget Address entity, AddressRequest request);
}
