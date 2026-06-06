package com.mokah.veterinary.features.diagnosis.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository repository;
    private final DiagnosisMapper mapper;

    @Override
    public DiagnosisResponse create(DiagnosisRequest dto) {
        Diagnosis entity = mapper.toEntity(dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Diagnosis entityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));
    }

    @Override
    public List<DiagnosisResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public DiagnosisResponse findById(Long id) {
        Diagnosis entity = entityById(id);

        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        Diagnosis entity = entityById(id);
        repository.delete(entity);
    }
}
