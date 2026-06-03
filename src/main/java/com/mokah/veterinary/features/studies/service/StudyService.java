package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;

import java.util.List;

public interface StudyService {
       StudyResponse create(StudyRequest request);
       List<StudyResponse> findAll();
       StudyResponse findById(Long id);
       StudyResponse update(Long id, StudyRequest request);
       void delete(Long id) ;
}
