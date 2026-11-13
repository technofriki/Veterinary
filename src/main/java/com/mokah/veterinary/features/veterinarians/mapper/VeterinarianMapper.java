package com.mokah.veterinary.features.veterinarians.mapper;

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianPublicResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianView;
import com.mokah.veterinary.security.ProfessionalVisibility;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface VeterinarianMapper {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "workStartTime", target = "workStartTime")
    @Mapping(source = "workEndTime", target = "workEndTime")
    @Mapping(source = "workDays", target = "workDays")
    VeterinarianResponse toResponse(Veterinarian entity);
    List<VeterinarianResponse> toResponseList(List<Veterinarian> entities);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    VeterinarianPublicResponse toPublicResponse(Veterinarian entity);

    @Named("visibleVet")
    default VeterinarianView toVisibleResponse(Veterinarian entity) {
        if (entity == null) return null;
        return ProfessionalVisibility.isAdmin() ? toResponse(entity) : toPublicResponse(entity);
    }


    Veterinarian toEntity(VeterinarianCreateDTO dto);

    void update(@MappingTarget Veterinarian entity, VeterinarianUpdateDTO dto);
}
