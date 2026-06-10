package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentCreateDTO(
        @NotNull LocalDateTime appointmentDate,
        @NotBlank String reason,
        @NotNull UUID petExternalId,
        @NotNull UUID veterinarianExternalId,
        @NotNull UUID branchExternalId
) {
}