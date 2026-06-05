package com.mokah.veterinary.features.visits.mapper;

import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.entity.VisitEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VeterinarianMapper.class, AppointmentMapper.class})
public interface VisitMapper {
    VisitEntity toEntity (VisitRequest request);
    VisitResponse toResponse (VisitEntity entity);
    List<VisitResponse> toResponseList (List<VisitEntity> entities);

}
