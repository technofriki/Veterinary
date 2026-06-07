package com.mokah.veterinary.features.studies.mapper;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.model.Study;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyMapper {
    Study toEntity(StudyRequest request);

    StudyResponse toResponse(Study entity);

    List<StudyResponse> toResponseList(List<Study> entities);

    void updateEntity(@MappingTarget Study entity, StudyRequest request);
}
