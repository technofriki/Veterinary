package com.mokah.veterinary.features.prescriptions.dto;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;

import java.time.LocalDate;
import java.util.UUID;

public record PrescriptionResponse(
        UUID externalId,
        Long quantity,
        String indication,
        LocalDate expirationDate,
        Boolean active,
        MedicationResponse medication,
        DiagnosisResponse diagnosis
) {
}
