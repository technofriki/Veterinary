package com.mokah.veterinary.features.visits.dto;

import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;

import java.util.UUID;

public record VisitResponse(
        UUID externalId,
        String observations,
        AppointmentResponse appointment,
        VeterinarianResponse veterinarian
) {
}
