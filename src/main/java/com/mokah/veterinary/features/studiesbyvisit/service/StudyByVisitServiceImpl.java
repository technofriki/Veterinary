package com.mokah.veterinary.features.studiesbyvisit.service;


import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.studies.model.Study;
import com.mokah.veterinary.features.studies.service.StudyService;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.exception.StudyByVisitExistsException;
import com.mokah.veterinary.features.studiesbyvisit.mapper.StudyByVisitMapper;
import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;
import com.mokah.veterinary.features.studiesbyvisit.repository.StudyByVisitRepository;
import com.mokah.veterinary.features.visits.model.Visit;
import com.mokah.veterinary.features.visits.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyByVisitServiceImpl implements StudyByVisitService {

    private final StudyByVisitRepository repository;
    private final StudyByVisitMapper mapper;
    private final StudyService studyService;
    private final VisitService visitService;

    @Override
    public StudyByVisitResponse create(StudyByVisitDTO dto) {

        if (repository.existsByStudyExternalIdAndVisitExternalId(dto.studyExternalId(), dto.visitExternalId())) {
            throw new StudyByVisitExistsException(
                    "This study is already associated with the visit"
            );
        }

        StudyByVisit entity = mapper.toEntity(dto);

        entity.setStudy(studyService.entityByExternalId(dto.studyExternalId()));

        entity.setVisit(visitService.entityByExternalId(dto.visitExternalId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public StudyByVisit entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyByVisit", "externalId", externalId));
    }

    @Override
    public StudyByVisitResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<StudyByVisitResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public void deactivate(UUID externalId) {
        StudyByVisit entity = entityByExternalId(externalId);

        entity.setActive(false);

        repository.save(entity);
    }
}