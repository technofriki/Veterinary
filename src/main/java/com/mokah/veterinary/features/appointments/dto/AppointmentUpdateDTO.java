package com.mokah.veterinary.features.appointments.dto;

import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
        @NotNull LocalDateTime appointmentDate,
        @NotBlank String reason,
        @NotNull AppointmentStatus status,
        @NotNull @Positive Long petId,
        @NotNull @Positive Long veterinarianId
) {}