package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.entity.Study;
import com.mokah.veterinary.features.studies.mapper.StudyMapper;
import com.mokah.veterinary.features.studies.repository.StudyRepository;
import com.mokah.veterinary.features.studies.specification.StudySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;

    @Override
    public StudyResponse create(StudyRequest request) {
        Study entity = studyMapper.toEntity(request);
        return studyMapper.toResponse(studyRepository.save(entity));
    }

    @Override
    public List<StudyResponse> findAll(String name, String description) {
        PredicateSpecification<Study> spec = PredicateSpecification.allOf(
                StudySpecification.hasName(name),
                StudySpecification.hasDescription(description)
        );
        return studyMapper.toResponseList(studyRepository.findAll(spec));
    }

    @Override
    public StudyResponse findById(Long id) {
        return studyMapper.toResponse(entityById(id));
    }

    @Override
    public Study entityById(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study", id));
    }

    @Override
    public StudyResponse update(Long id, StudyRequest request) {
        Study entity = entityById(id);
        studyMapper.updateEntity(entity, request);

        return studyMapper.toResponse(studyRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Study entity = entityById(id);
        studyRepository.delete(entity);
    }

}
