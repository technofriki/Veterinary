package com.mokah.veterinary.features.visits.dto;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record VisitRequest(
        @NotBlank String observations,
        @NotNull @Positive Long appointmentId,
        @NotNull @Positive Long veterinarianId
) {
}
