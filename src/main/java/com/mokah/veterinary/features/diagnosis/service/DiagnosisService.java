package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;

import java.util.List;
import java.util.UUID;

public interface DiagnosisService {

    DiagnosisResponse create(DiagnosisRequest dto);

    Diagnosis entityByExternalId(UUID externalId);

    List<DiagnosisResponse> findAll();

    DiagnosisResponse findById(UUID externalId);

    DiagnosisResponse update(UUID externalId, DiagnosisRequest dto);

    void delete(UUID externalId);
}
