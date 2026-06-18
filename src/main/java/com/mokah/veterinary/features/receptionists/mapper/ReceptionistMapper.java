package com.mokah.veterinary.features.receptionists.mapper;

import com.mokah.veterinary.features.receptionists.dto.ReceptionistCreateDTO;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistUpdateDTO;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReceptionistMapper {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    ReceptionistResponse toResponse(Receptionist entity);
    List<ReceptionistResponse> toResponseList(List<Receptionist> entities);

    Receptionist toEntity(ReceptionistCreateDTO dto);

    void update(@MappingTarget Receptionist entity, ReceptionistUpdateDTO dto);
}
