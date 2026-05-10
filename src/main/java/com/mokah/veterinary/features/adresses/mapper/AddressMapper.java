package com.mokah.veterinary.features.adresses.mapper;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import com.mokah.veterinary.features.adresses.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    AddressEntity toEntity (AddressRequest request);

    AddressResponse toResponse(AddressEntity entity);
}
