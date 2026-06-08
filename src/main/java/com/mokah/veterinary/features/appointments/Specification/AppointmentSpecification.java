package com.mokah.veterinary.features.appointments.Specification;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentSpecification {

    public static PredicateSpecification<Appointment> hasAppointmentDate(
            LocalDateTime appointmentDate
    ) {
        return (from, cb) -> appointmentDate == null
                ? cb.conjunction()
                : cb.equal(
                from.get("appointmentDate"),
                appointmentDate
        );
    }

    public static PredicateSpecification<Appointment> hasReason(
            String reason
    ) {
        return (from, cb) -> reason == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("reason")),
                "%" + reason.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Appointment> hasStatus(
            AppointmentStatus status
    ) {
        return (from, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(
                from.get("status"),
                status
        );
    }

    public static PredicateSpecification<Appointment> hasPetExternalId(
            UUID petExternalId
    ) {
        return (from, cb) -> petExternalId == null
                ? cb.conjunction()
                : cb.equal(
                from.get("pet").get("externalId"),
                petExternalId
        );
    }

    public static PredicateSpecification<Appointment> hasVeterinarianExternalId(
            UUID veterinarianExternalId
    ) {
        return (from, cb) -> veterinarianExternalId == null
                ? cb.conjunction()
                : cb.equal(
                from.get("veterinarian").get("externalId"),
                veterinarianExternalId
        );
    }
}