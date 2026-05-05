package com.mokah.veterinary.features.diagnosis.mapper;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequestDTO;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponseDTO;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {

    DiagnosisResponseDTO toResponse(Diagnosis entity);

    List<DiagnosisResponseDTO> toResponseList(List<Diagnosis> entities);

    @Mapping(target = "id", ignore = true)
    Diagnosis toEntity(DiagnosisRequestDTO request);

    @Mapping(target = "id",ignore = true)
    void updateEntity(@MappingTarget Diagnosis entity, DiagnosisRequestDTO request);
}
