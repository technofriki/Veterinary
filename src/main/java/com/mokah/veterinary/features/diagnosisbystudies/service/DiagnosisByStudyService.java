package com.mokah.veterinary.features.diagnosisbystudies.service;

import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;

import java.util.List;
import java.util.UUID;

public interface DiagnosisByStudyService {

    DiagnosisByStudyResponse create(DiagnosisByStudyDTO dto);

    DiagnosisByStudy entityByExternalId(UUID externalId);

    DiagnosisByStudyResponse findById(UUID externalId);

    List<DiagnosisByStudyResponse> findAll();

    void deactivate(UUID externalId);
}
