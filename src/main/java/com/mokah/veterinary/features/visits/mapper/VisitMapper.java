package com.mokah.veterinary.features.visits.mapper;

import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.entity.VisitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VeterinarianMapper.class, AppointmentMapper.class})
public interface VisitMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinarian", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    VisitEntity toEntity (VisitRequest request);
    VisitResponse toResponse (VisitEntity entity);
    List<VisitResponse> toResponseList (List<VisitEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinarian", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    void update(@MappingTarget VisitEntity entity, VisitRequest request);
}
