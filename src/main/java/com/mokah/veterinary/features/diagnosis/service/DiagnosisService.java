package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequestDTO;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponseDTO;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponseDTO create(DiagnosisRequestDTO request);

    List<DiagnosisResponseDTO> findAll();

    DiagnosisResponseDTO findById(Long id);

    void delete(Long id);
}
