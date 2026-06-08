package com.mokah.veterinary.features.prescriptions.dto;

import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import com.mokah.veterinary.features.medication.model.Medication;

public record PrescriptionResponse(
        Long quantity,
        String indication,
        Medication medication,
        Diagnosis diagnosis
) {
}
