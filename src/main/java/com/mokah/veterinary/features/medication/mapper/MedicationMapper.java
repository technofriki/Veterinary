package com.mokah.veterinary.features.medication.mapper;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.entity.MedicationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    MedicationEntity toEntity(MedicationRequest request);
    MedicationResponse toResponse(MedicationEntity medicationEntity);
}
