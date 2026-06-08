package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponse create(AppointmentCreateDTO dto);

    List<AppointmentResponse> findAll(
            LocalDateTime date,
            String reason,
            AppointmentStatus status,
            UUID petExternalId,
            UUID veterinarianExternalId
    );

    Appointment entityByExternalId(UUID externalId);

    AppointmentResponse findById(UUID externalId);

    AppointmentResponse update(
            UUID externalId,
            AppointmentUpdateDTO dto
    );

    void delete(UUID externalId);
}
