package com.mokah.veterinary.features.medication.service;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.model.Medication;

import java.util.List;
import java.util.UUID;

public interface MedicationService {

    MedicationResponse create(MedicationRequest dto);

    List<MedicationResponse> findAll();

    Medication entityByExternalId(UUID externalId);

    MedicationResponse findById(UUID externalId);

    MedicationResponse findByName(String name);

    MedicationResponse update(
            UUID externalId,
            MedicationRequest dto
    );

    void delete(UUID externalId);
}
