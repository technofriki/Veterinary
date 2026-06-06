package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.visits.entity.VisitEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse create(AppointmentCreateDTO dto);

    List<AppointmentResponse> findAll(
            LocalDateTime date,
            String reason,
            AppointmentStatus status,
            Long petId,
            Long veterinarianId
    );
    Appointment entityById(Long id);
    AppointmentResponse findById(Long id);
    AppointmentResponse update(Long id, AppointmentUpdateDTO dto);

    void delete(Long id);
}
