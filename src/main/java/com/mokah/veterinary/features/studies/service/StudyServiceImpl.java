package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.model.Study;
import com.mokah.veterinary.features.studies.mapper.StudyMapper;
import com.mokah.veterinary.features.studies.repository.StudyRepository;
import com.mokah.veterinary.features.studies.specification.StudySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository repository;
    private final StudyMapper mapper;

    @Override
    public StudyResponse create(StudyRequest dto) {
        Study entity = mapper.toEntity(dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<StudyResponse> findAll(
            String name,
            String description) {

        PredicateSpecification<Study> spec =
                PredicateSpecification.allOf(
                        StudySpecification.hasName(name),
                        StudySpecification.hasDescription(description)
                );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    public Study entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", "externalId", externalId));
    }

    @Override
    public StudyResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public StudyResponse update(
            UUID externalId,
            StudyRequest dto) {

        Study entity = entityByExternalId(externalId);

        mapper.updateEntity(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}
