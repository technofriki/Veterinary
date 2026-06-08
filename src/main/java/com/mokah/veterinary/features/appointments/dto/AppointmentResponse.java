package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID externalId,
        LocalDateTime appointmentDate,
        String reason,
        String status,
        PetResponse pet,
        VeterinarianResponse veterinarian
) {
}