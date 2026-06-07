package com.mokah.veterinary.features.prescriptions.dto;

import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.medication.entity.MedicationEntity;

public record PrescriptionResponse(
        Long quantity,
        String indication,
        MedicationEntity medication,
        Diagnosis diagnosis
) {
}
