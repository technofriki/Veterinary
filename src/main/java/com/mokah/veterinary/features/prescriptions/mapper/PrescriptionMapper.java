package com.mokah.veterinary.features.prescriptions.mapper;

import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "medication", ignore = true)
    @Mapping(target = "diagnosis", ignore = true)
    Prescription toEntity(PrescriptionRequest dto);

    PrescriptionResponse toResponse(Prescription entity);

    List<PrescriptionResponse> toResponseList(List<Prescription> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "medication", ignore = true)
    @Mapping(target = "diagnosis", ignore = true)
    void update(
            @MappingTarget Prescription entity,
            PrescriptionRequest dto
    );
}
