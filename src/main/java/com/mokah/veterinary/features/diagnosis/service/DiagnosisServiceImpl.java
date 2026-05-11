package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service("diagnosisService")
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisMapper diagnosisMapper;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository, DiagnosisMapper diagnosisMapper) {
        this.diagnosisRepository = diagnosisRepository;
        this.diagnosisMapper = diagnosisMapper;
    }

    @Override
    public DiagnosisResponse create(DiagnosisRequest request) {
        Diagnosis diagnosis = diagnosisMapper.toEntity(request);
        Diagnosis savedDiagnosis = diagnosisRepository.save(diagnosis);
        return diagnosisMapper.toResponse(savedDiagnosis);
    }

    @Override
    public List<DiagnosisResponse> findAll() {
        return diagnosisMapper.toResponseList(diagnosisRepository.findAll());
    }

    @Override
    public DiagnosisResponse findById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));

        return diagnosisMapper.toResponse(diagnosis);
    }

    @Override
    public void delete(Long id) {
        if (!diagnosisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Diagnosis", id);
        }
        diagnosisRepository.deleteById(id);
    }
}
