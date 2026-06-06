package com.mokah.veterinary.features.diagnosisbystudies.mapper;

import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiagnosisByStudyMapper {

    @Mapping(target = "diagnosis", ignore = true)
    @Mapping(target = "study", ignore = true)
    @Mapping(target = "active", ignore = true)
    DiagnosisByStudy toEntity(DiagnosisByStudyDTO dto);

    DiagnosisByStudyResponse toResponse(DiagnosisByStudy entity);
    List<DiagnosisByStudyResponse> toResponseList(List<DiagnosisByStudy> entities);
}
