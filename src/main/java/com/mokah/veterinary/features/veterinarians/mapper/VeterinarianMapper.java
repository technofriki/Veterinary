package com.mokah.veterinary.features.veterinarians.mapper;

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring")
public interface VeterinarianMapper {
    VeterinarianResponse toResponse(Veterinarian entity);

    List<VeterinarianResponse> toResponseList(List<Veterinarian> entities);

    Veterinarian toEntity(VeterinarianCreateDTO request);

    void update(@MappingTarget Veterinarian entity, VeterinarianUpdateDTO request);
}
