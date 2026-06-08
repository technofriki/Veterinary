package com.mokah.veterinary.features.medication.dto;

import java.util.UUID;

public record MedicationResponse(
        UUID externalId,
        String name,
        Double dosage,
        String dosageUnit
) {
}
