package com.mokah.veterinary.features.appointments.mapper;

import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toEntity(AppointmentCreateDTO request);

    AppointmentResponse toResponse(Appointment entity);

    List<AppointmentResponse> toResponseList(List<Appointment> entities);

    AppointmentResponse update(@MappingTarget Appointment entity, AppointmentUpdateDTO request);
}
