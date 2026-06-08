package com.mokah.veterinary.features.prescriptions.service;

import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.model.Prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionService {

    PrescriptionResponse create(PrescriptionRequest request);

    Prescription entityByExternalId(UUID externalId);

    PrescriptionResponse findById(UUID externalId);

    List<PrescriptionResponse> findAll(
            UUID diagnosisExternalId,
            UUID petExternalId
    );

}