package com.mokah.veterinary.features.diagnosis.mapper;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {

    DiagnosisResponse toResponse(Diagnosis entity);

    List<DiagnosisResponse> toResponseList(List<Diagnosis> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "visit", ignore = true)
    Diagnosis toEntity(DiagnosisRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "visit", ignore = true)
    void update(
            @MappingTarget Diagnosis entity,
            DiagnosisRequest request
    );
}
