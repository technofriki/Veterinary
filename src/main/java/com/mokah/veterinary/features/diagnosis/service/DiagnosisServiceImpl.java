package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import com.mokah.veterinary.features.visits.service.VisitService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository repository;
    private final DiagnosisMapper mapper;
    private final VisitService visitService;

    @Override
    public Diagnosis entityByExternalId(
            UUID externalId
    ) {

        return repository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Diagnosis",
                                "externalId",
                                externalId
                        ));
    }

    @Override
    public DiagnosisResponse findById(
            UUID externalId
    ) {

        return mapper.toResponse(
                entityByExternalId(externalId)
        );
    }

    @Override
    public DiagnosisResponse create(
            DiagnosisRequest request
    ) {

        Diagnosis entity =
                mapper.toEntity(request);

        entity.setVisit(
                visitService.entityByExternalId(
                        request.visitExternalId()
                )
        );

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    @Override
    public List<DiagnosisResponse> findAll() {
        return mapper.toResponseList(
                repository.findAll()
        );
    }

    @Override
    public DiagnosisResponse update(
            UUID externalId,
            DiagnosisRequest request
    ) {

        Diagnosis entity =
                entityByExternalId(externalId);

        mapper.update(entity, request);

        entity.setVisit(
                visitService.entityByExternalId(
                        request.visitExternalId()
                )
        );

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    @Override
    public void delete(
            UUID externalId
    ) {

        repository.delete(
                entityByExternalId(externalId)
        );
    }
}
