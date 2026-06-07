package com.mokah.veterinary.features.studiesbyvisit.service;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;

import java.util.List;

public interface StudyByVisitService {
    StudyByVisitResponse create(StudyByVisitDTO dto);

    StudyByVisit entityById(Long id);
    StudyByVisitResponse findById(Long id);
    List<StudyByVisitResponse> findAll();

    void deactivate(Long id);
}
