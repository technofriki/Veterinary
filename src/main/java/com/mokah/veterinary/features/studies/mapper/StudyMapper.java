package com.mokah.veterinary.features.studies.mapper;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.model.Study;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyMapper {

    Study toEntity(StudyRequest dto);

    StudyResponse toResponse(Study entity);

    List<StudyResponse> toResponseList(List<Study> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    void updateEntity(
            @MappingTarget Study entity,
            StudyRequest dto
    );
}
