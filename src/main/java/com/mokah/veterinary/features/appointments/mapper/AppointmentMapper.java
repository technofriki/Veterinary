package com.mokah.veterinary.features.appointments.mapper;

import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinarian", ignore = true)
    Appointment toEntity(AppointmentCreateDTO dto);

    AppointmentResponse toResponse(Appointment entity);

    List<AppointmentResponse> toResponseList(List<Appointment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinarian", ignore = true)
    void update(@MappingTarget Appointment entity, AppointmentUpdateDTO dto);
}
