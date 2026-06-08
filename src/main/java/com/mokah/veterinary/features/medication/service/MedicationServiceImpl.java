package com.mokah.veterinary.features.medication.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.model.Medication;
import com.mokah.veterinary.features.medication.mapper.MedicationMapper;
import com.mokah.veterinary.features.medication.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository repository;
    private final MedicationMapper mapper;

    @Override
    public Medication entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "externalId", externalId));
    }

    @Override
    public MedicationResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<MedicationResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public MedicationResponse findByName(String name) {
        Medication entity = repository.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Medication", "name", name));

        return mapper.toResponse(entity);
    }

    @Override
    public MedicationResponse create(MedicationRequest dto) {
        Medication entity = mapper.toEntity(dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public MedicationResponse update(
            UUID externalId,
            MedicationRequest dto) {

        Medication entity =
                entityByExternalId(externalId);

        entity.setName(dto.name());
        entity.setDosage(dto.dosage());
        entity.setDosageUnit(dto.dosageUnit());

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId){
        repository.delete(entityByExternalId(externalId));
    }
}
