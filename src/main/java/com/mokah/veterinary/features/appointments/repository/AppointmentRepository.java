package com.mokah.veterinary.features.appointments.repository;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    Optional<Appointment> findByExternalId(UUID externalId);
    Boolean existsByVeterinarian_ExternalIdAndAppointmentDateAndStatus(
            UUID veterinarianExternalId,
            LocalDateTime appointmentDate,
            AppointmentStatus status
    );

    Boolean existsByPet_ExternalIdAndAppointmentDateAndStatus(
            UUID petExternalId,
            LocalDateTime appointmentDate,
            AppointmentStatus status
    );
    Boolean existsByVeterinarian_ExternalIdAndAppointmentDateAndStatusAndExternalIdNot(
            UUID veterinarianExternalId,
            LocalDateTime appointmentDate,
            AppointmentStatus status,
            UUID externalIdNot
    );

    Boolean existsByPet_ExternalIdAndAppointmentDateAndStatusAndExternalIdNot(
            UUID petExternalId,
            LocalDateTime appointmentDate,
            AppointmentStatus status,
            UUID externalIdNot
    );
}
