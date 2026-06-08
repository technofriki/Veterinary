package com.mokah.veterinary.features.studiesbyvisit.service;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;

import java.util.List;
import java.util.UUID;

public interface StudyByVisitService {

    StudyByVisitResponse create(StudyByVisitDTO dto);

    StudyByVisit entityByExternalId(UUID externalId);

    StudyByVisitResponse findById(UUID externalId);

    List<StudyByVisitResponse> findAll();

    void deactivate(UUID externalId);
}
