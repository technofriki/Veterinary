package com.mokah.veterinary.features.visits.dto;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;

public record VisitResponse(
        String observations,
        Appointment appointment,
        Veterinarian veterinarian
) {

}
