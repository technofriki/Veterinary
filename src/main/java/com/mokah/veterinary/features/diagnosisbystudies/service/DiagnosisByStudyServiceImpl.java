package com.mokah.veterinary.features.diagnosisbystudies.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.exception.DiagnosisByStudyExistsException;
import com.mokah.veterinary.features.diagnosisbystudies.mapper.DiagnosisByStudyMapper;
import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;
import com.mokah.veterinary.features.diagnosisbystudies.repository.DiagnosisByStudyRepository;
import com.mokah.veterinary.features.studies.model.Study;
import com.mokah.veterinary.features.studies.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisByStudyServiceImpl implements DiagnosisByStudyService {

    private final DiagnosisByStudyRepository repository;
    private final DiagnosisByStudyMapper mapper;
    private final DiagnosisService diagnosisService;
    private final StudyService studyService;

    @Override
    public DiagnosisByStudyResponse create(
            DiagnosisByStudyDTO dto
    ) {

        Diagnosis diagnosis =
                diagnosisService.entityByExternalId(
                        dto.diagnosisExternalId()
                );

        Study study =
                studyService.entityByExternalId(
                        dto.studyExternalId()
                );

        if (repository.existsByDiagnosisIdAndStudyId(
                diagnosis.getId(),
                study.getId()
        )) {

            throw new DiagnosisByStudyExistsException(
                    "Diagnosis with that study already exists"
            );
        }

        DiagnosisByStudy entity = mapper.toEntity(dto);

        entity.setDiagnosis(diagnosis);
        entity.setStudy(study);
        entity.setActive(true);

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    @Override
    public DiagnosisByStudy entityByExternalId(
            UUID externalId
    ) {

        return repository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DiagnosisByStudy",
                                "externalId",
                                externalId
                        )
                );
    }

    @Override
    public DiagnosisByStudyResponse findById(
            UUID externalId
    ) {

        return mapper.toResponse(
                entityByExternalId(externalId)
        );
    }

    @Override
    public List<DiagnosisByStudyResponse> findAll() {
        return mapper.toResponseList(
                repository.findAll()
        );
    }

    @Override
    public void deactivate(
            UUID externalId
    ) {

        DiagnosisByStudy entity =
                entityByExternalId(externalId);

        entity.setActive(false);

        repository.save(entity);
    }
}