package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponse create(DiagnosisRequest request);

    List<DiagnosisResponse> findAll();

    DiagnosisResponse findById(Long id);

    void delete(Long id);
}
