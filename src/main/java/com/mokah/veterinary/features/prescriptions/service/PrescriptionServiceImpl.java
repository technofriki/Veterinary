package com.mokah.veterinary.features.prescriptions.service;

import com.mokah.veterinary.common.exception.InvalidDateException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import com.mokah.veterinary.features.medication.service.MedicationService;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.mapper.PrescriptionMapper;
import com.mokah.veterinary.features.prescriptions.model.Prescription;
import com.mokah.veterinary.features.prescriptions.repository.PrescriptionRepository;
import com.mokah.veterinary.features.prescriptions.specification.PrescriptionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository repository;
    private final PrescriptionMapper mapper;
    private final MedicationService medicationService;
    private final DiagnosisService diagnosisService;

    @Override
    public Prescription entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "externalId", externalId));
    }

    @Override
    public PrescriptionResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<PrescriptionResponse> findAll(
            UUID diagnosisExternalId,
            UUID petExternalId) {

        PredicateSpecification<Prescription> spec =
                PredicateSpecification.allOf(
                        PrescriptionSpecification.hasDiagnosisExternalId(
                                diagnosisExternalId
                        ),
                        PrescriptionSpecification.hasPetExternalId(
                                petExternalId
                        )
                );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    public PrescriptionResponse create(PrescriptionRequest request) {

        Diagnosis diagnosis = diagnosisService.entityByExternalId(request.diagnosisExternalId());

        LocalDate dateControl = diagnosis.getDateDiagnosis().toLocalDate();
        LocalDate expirationDate = dateControl.plusMonths(1);

        if(LocalDate.now().isAfter(expirationDate)) {
            throw new InvalidDateException("Expiration date must be one month after now");
        }


        Prescription entity = mapper.toEntity(request);

        entity.setDiagnosis(diagnosisService.entityByExternalId(request.diagnosisExternalId()));

        entity.setMedication(medicationService.entityByExternalId(request.medicationExternalId()));

        return mapper.toResponse(repository.save(entity));
    }
}