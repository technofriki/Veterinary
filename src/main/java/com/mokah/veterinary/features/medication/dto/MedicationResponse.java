package com.mokah.veterinary.features.medication.dto;

public record MedicationResponse(
        Long id,
        String name,
        Double dosage,
        String dosageUnit
) {
}
