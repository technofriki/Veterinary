package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentUpdateDTO(
        @NotNull LocalDateTime appointmentDate,
        @NotNull @Min(15) @Max(120) Integer durationMinutes,
        @NotBlank String reason,
        @NotNull AppointmentStatus status,
        @NotNull UUID petExternalId,
        @NotNull UUID veterinarianExternalId,
        @NotNull UUID branchExternalId
) {
}