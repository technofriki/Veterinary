package com.mokah.veterinary.features.visits.dto;

import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitResponse(
        UUID externalId,
        String observations,
        AppointmentResponse appointment,
        VeterinarianResponse veterinarian,
        PetResponse pet,
        LocalDateTime visitDate
) {
}