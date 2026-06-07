package com.mokah.veterinary.features.prescriptions.service;

import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.model.Prescription;

import java.util.List;

public interface PrescriptionServiceInterface {

    PrescriptionResponse create(PrescriptionRequest request);
    Prescription entityById(Long id);
    PrescriptionResponse findById(Long id);
    List<PrescriptionResponse> findAll(Long diagnosisId, Long petId);

}
