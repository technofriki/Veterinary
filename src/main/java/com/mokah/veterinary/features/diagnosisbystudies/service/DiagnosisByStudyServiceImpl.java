package com.mokah.veterinary.features.diagnosisbystudies.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.exception.DiagnosisByStudyExistsException;
import com.mokah.veterinary.features.diagnosisbystudies.mapper.DiagnosisByStudyMapper;
import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;
import com.mokah.veterinary.features.diagnosisbystudies.repository.DiagnosisByStudyRepository;
import com.mokah.veterinary.features.studies.entity.Study;
import com.mokah.veterinary.features.studies.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisByStudyServiceImpl implements DiagnosisByStudyService {

    private final DiagnosisByStudyRepository repository;
    private final DiagnosisByStudyMapper mapper;
    private final DiagnosisService diagnosisService;
    private final StudyService studyService;

    @Override
    public DiagnosisByStudyResponse create(DiagnosisByStudyDTO dto) {
        if (repository.existsByDiagnosisIdAndStudyId(dto.diagnosisId(), dto.studyId())) {
            throw new DiagnosisByStudyExistsException("Diagnosis with that study already exists");
        }

        DiagnosisByStudy entity = mapper.toEntity(dto);
        Diagnosis diagnosis = diagnosisService.entityById(dto.diagnosisId());
        Study study = studyService.entityById(dto.studyId());

        entity.setDiagnosis(diagnosis);
        entity.setStudy(study);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public DiagnosisByStudy entityById(Long id) {
        return repository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Diagnosis by Study", id));
    }

    @Override
    public DiagnosisByStudyResponse findById(Long id) {
        DiagnosisByStudy entity = entityById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public List<DiagnosisByStudyResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public void deactivate(Long id) {
        DiagnosisByStudy entity = entityById(id);
        entity.setActive(false);
        repository.save(entity);
    }
}
