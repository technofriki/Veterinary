package com.mokah.veterinary.features.appointments.repository;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    Optional<Appointment> findByExternalId(UUID externalId);

    List<Appointment> findByPet_ExternalIdAndStatusIn(
            UUID petExternalId,
            List<AppointmentStatus> statuses
    );

    List<Appointment> findByVeterinarian_ExternalIdAndStatusIn(
            UUID veterinarianExternalId,
            List<AppointmentStatus> statuses
    );
}
