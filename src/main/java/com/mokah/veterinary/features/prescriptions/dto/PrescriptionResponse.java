package com.mokah.veterinary.features.prescriptions.dto;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;

import java.util.UUID;

public record PrescriptionResponse(
        UUID externalId,
        Long quantity,
        String indication,
        MedicationResponse medication,
        DiagnosisResponse diagnosis
) {
}
