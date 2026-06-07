package com.mokah.veterinary.features.studiesbyvisit.mapper;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudyByVisitMapper {

    @Mapping(target = "study", ignore = true)
    @Mapping(target = "visit", ignore = true)
   StudyByVisit toEntity(StudyByVisitDTO dto);

   StudyByVisitResponse toResponse(StudyByVisit entity);
   List<StudyByVisitResponse> toResponseList(List<StudyByVisit> entities);
}
