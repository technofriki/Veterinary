package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentUpdateDTO(
        @NotNull LocalDateTime appointmentDate,
        @NotBlank String reason,
        @NotNull AppointmentStatus status,
        @NotNull UUID petExternalId,
        @NotNull UUID veterinarianExternalId
) {
}