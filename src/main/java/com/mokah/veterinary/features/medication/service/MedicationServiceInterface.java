package com.mokah.veterinary.features.medication.service;

import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;

import java.util.List;

public interface MedicationServiceInterface {
    MedicationResponse create(MedicationRequest request);
    List<MedicationResponse> findAll();
    MedicationResponse findById(Long id);
    MedicationResponse findByName(String name);
    MedicationResponse update(Long id, MedicationRequest request);
    void delete(Long id);
}
