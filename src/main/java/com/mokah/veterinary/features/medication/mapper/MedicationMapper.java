package com.mokah.veterinary.features.medication.mapper;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.model.Medication;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    Medication toEntity(MedicationRequest dto);

    MedicationResponse toResponse(Medication entity);

    List<MedicationResponse> toResponseList(List<Medication> entities);
}
