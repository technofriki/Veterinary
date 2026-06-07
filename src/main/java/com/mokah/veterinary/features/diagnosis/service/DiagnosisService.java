package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponse create(DiagnosisRequest request);

    Diagnosis entityById(Long id);

    List<DiagnosisResponse> findAll();

    DiagnosisResponse findById(Long id);

    Diagnosis entityById(Long id);
}
