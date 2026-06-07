package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import com.mokah.veterinary.features.visits.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service("diagnosisService")
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisMapper diagnosisMapper;
    private final VisitService visitService;

    @Override
    public Diagnosis entityById(Long id){
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis no found with id: ", id));
    }

    @Override
    public DiagnosisResponse findById(Long id) {
        return diagnosisMapper.toResponse(entityById(id));
    }
    @Override
    public DiagnosisResponse create(DiagnosisRequest request) {
        Diagnosis diagnosis = diagnosisMapper.toEntity(request);
        diagnosis.setVisit(visitService.entityById(request.visitId()));
        return diagnosisMapper.toResponse(diagnosisRepository.save(diagnosis));
    }

    @Override
    public List<DiagnosisResponse> findAll() {
        return diagnosisMapper.toResponseList(diagnosisRepository.findAll());
    }



}
