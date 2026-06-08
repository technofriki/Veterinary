package com.mokah.veterinary.features.medication.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.model.Medication;
import com.mokah.veterinary.features.medication.mapper.MedicationMapper;
import com.mokah.veterinary.features.medication.repository.MedicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationService implements MedicationServiceInterface{

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    public MedicationService(MedicationRepository medicationRepository, MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }


    @Override
    public Medication entityById(Long id){

        return medicationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Medication no found with id: ", id));
    }
    @Override
    public MedicationResponse findById(Long id){
        return medicationMapper.toResponse(entityById(id));
    }
    @Override
    public List<MedicationResponse> findAll(){
        return medicationRepository.findAll().stream()
                .map(medicationMapper::toResponse)
                .collect(Collectors.toList());
    }



    @Override
    public MedicationResponse findByName(String name){
        Medication entity = medicationRepository.findAll().stream()
                .filter(e->e.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Medication not found with name: "+name));
        return medicationMapper.toResponse(entity);
    }

    @Override
    public MedicationResponse create (MedicationRequest request){
        Medication entity = medicationMapper.toEntity(request);
        Medication saved = medicationRepository.save(entity);
        return medicationMapper.toResponse(saved);
    }

    @Override
    public MedicationResponse update (Long id, MedicationRequest request){
        Medication existingMedication = medicationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Medication not found with ID: "+id));
    existingMedication.setName(request.name());
    existingMedication.setDosage(request.dosage());
    existingMedication.setDosageUnit(request.dosageUnit());

    Medication updated = medicationRepository.save(existingMedication);
    return medicationMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id){
        Medication entity = medicationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Medication not found with ID: "+id));
    medicationRepository.delete(entity);

    }
}
