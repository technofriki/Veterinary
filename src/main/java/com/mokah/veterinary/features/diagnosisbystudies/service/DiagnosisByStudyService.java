package com.mokah.veterinary.features.diagnosisbystudies.service;

import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;

import java.util.List;

public interface DiagnosisByStudyService {
    DiagnosisByStudyResponse create(DiagnosisByStudyDTO dto);
    DiagnosisByStudy entityById(Long id);
    DiagnosisByStudyResponse findById(Long id);
    List<DiagnosisByStudyResponse> findAll();
    void deactivate(Long id);
}
