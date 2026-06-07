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

@Service
@RequiredArgsConstructor
public class StudyByVisitServiceImpl implements StudyByVisitService {

    private final StudyByVisitRepository repository;
    private final StudyByVisitMapper mapper;
    private final StudyService studyService;
    private final VisitService visitService;

    @Override
    public StudyByVisitResponse create(StudyByVisitDTO dto) {
        if (repository.existsByStudyIdAndVisitId(dto.studyId(), dto.visitId())) {
            throw new StudyByVisitExistsException("This study is already associated with the visit");
        }

        StudyByVisit entity = mapper.toEntity(dto);
        Study study = studyService.entityById(dto.studyId());
        Visit visit = visitService.entityById(dto.visitId());

        entity.setStudy(study);
        entity.setVisit(visit);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public StudyByVisit entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study by visit", id));
    }

    @Override
    public StudyByVisitResponse findById(Long id) {
        StudyByVisit entity = entityById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public List<StudyByVisitResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public void deactivate(Long id) {
        StudyByVisit entity = entityById(id);
        entity.setActive(false);
        repository.save(entity);
    }
}
