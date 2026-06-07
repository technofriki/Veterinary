package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.model.Study;

import java.util.List;

public interface StudyService {
       StudyResponse create(StudyRequest request);
       List<StudyResponse> findAll(String name, String description);
       StudyResponse findById(Long id);
       Study entityById(Long id);
       StudyResponse update(Long id, StudyRequest request);
       void delete(Long id) ;
}
