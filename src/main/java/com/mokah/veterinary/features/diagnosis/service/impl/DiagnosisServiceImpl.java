package com.mokah.veterinary.features.diagnosis.service.impl;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequestDTO;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponseDTO;
import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisMapper diagnosisMapper;

    @Override
    public DiagnosisResponseDTO create(DiagnosisRequestDTO request) {
        Diagnosis diagnosis = diagnosisMapper.toEntity(request);
        Diagnosis saved = diagnosisRepository.save(diagnosis);
        return diagnosisMapper.toResponse(saved);
    }

    @Override
    public List<DiagnosisResponseDTO> findAll() {
        return diagnosisMapper.toResponseList(diagnosisRepository.findAll());
    }

    @Override
    public DiagnosisResponseDTO findById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Diagnosis not found with id: " + id));

        return diagnosisMapper.toResponse(diagnosis);
    }

    @Override
    public void delete(Long id) {
        if (!diagnosisRepository.existsById(id)) {
            throw new NoSuchElementException("Diagnosis not found with id: " + id);
        }
        diagnosisRepository.deleteById(id);
    }
}
