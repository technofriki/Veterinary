package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;

import java.time.LocalDateTime;

public record AppointmentResponse(
        LocalDateTime appointmentDate,
        String reason,
        String status,
        PetResponse pet,
        VeterinarianResponse veterinarian
) {}