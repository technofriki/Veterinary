package com.mokah.veterinary.features.diagnosis.mapper;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequestDTO;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponseDTO;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;

public class DiagnosisMapper {

    public static Diagnosis toEntity(DiagnosisRequestDTO dto){
        return Diagnosis.builder()
                .descripcion(dto.getDescripcion())
                .fechaDiagnostico(dto.getFechaDiagnostico())
                .observaciones(dto.getObservaciones())
                .build();
    }

    public static DiagnosisResponseDTO toResponse(Diagnosis entity){
        return DiagnosisResponseDTO.builder()
                .id(entity.getId())
                .descripcion(entity.getDescripcion())
                .fechaDiagnostico(entity.getFechaDiagnostico())
                .observaciones(entity.getObservaciones())
                .build();
    }
}
