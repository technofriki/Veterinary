package com.mokah.veterinary.features.medication.service;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.model.Medication;

import java.util.List;

public interface MedicationServiceInterface {
    MedicationResponse create(MedicationRequest request);
    List<MedicationResponse> findAll();
    MedicationResponse findById(Long id);
    MedicationResponse findByName(String name);
    MedicationResponse update(Long id, MedicationRequest request);
    void delete(Long id);
    Medication entityById(Long id);
}
