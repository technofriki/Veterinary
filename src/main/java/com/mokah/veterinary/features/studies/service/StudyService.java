package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.model.Study;

import java.util.List;
import java.util.UUID;

public interface StudyService {

       StudyResponse create(StudyRequest dto);

       List<StudyResponse> findAll(
               String name,
               String description
       );

       StudyResponse findById(UUID externalId);

       Study entityByExternalId(UUID externalId);

       StudyResponse update(
               UUID externalId,
               StudyRequest dto
       );

       void delete(UUID externalId);
}
