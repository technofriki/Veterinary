package com.mokah.veterinary.features.prescriptions.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import com.mokah.veterinary.features.medication.service.MedicationServiceImpl;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.mapper.PrescriptionMapper;
import com.mokah.veterinary.features.prescriptions.model.Prescription;
import com.mokah.veterinary.features.prescriptions.repository.PrescriptionRepository;
import com.mokah.veterinary.features.prescriptions.specification.PrescriptionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final MedicationServiceImpl medicationServiceImpl;
    private final DiagnosisService diagnosisService;


    @Override
    public  Prescription entityById(Long id){
    return  prescriptionRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Prescription not found with id: "+id));
    }
    @Override
    public PrescriptionResponse findById(Long id){
return prescriptionMapper.toResponse(entityById(id));
    }

    @Override
    public List<PrescriptionResponse> findAll(Long diagnosisId, Long petId){
        PredicateSpecification<Prescription> specification = PredicateSpecification.allOf(
                PrescriptionSpecification.hasDiagnosisId(diagnosisId),
                PrescriptionSpecification.hasPetId(petId)
        );
        return prescriptionMapper.toResponseList(prescriptionRepository.findAll(specification));
    }
    @Override
    public PrescriptionResponse create(PrescriptionRequest request){
    Prescription entity = prescriptionMapper.toEntity(request);
    entity.setDiagnosis(diagnosisService.entityById(request.diagnosisId()));
    entity.setMedication(medicationServiceImpl.entityById(request.medicationId()));
    return  prescriptionMapper.toResponse(prescriptionRepository.save(entity));
    }


}
