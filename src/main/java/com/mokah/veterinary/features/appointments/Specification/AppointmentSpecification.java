package com.mokah.veterinary.features.appointments.Specification;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AppointmentSpecification {

    public static PredicateSpecification<Appointment> hasAppointmentDate(
            LocalDateTime appointmentDate) {
        return (from, cb) -> appointmentDate == null
                ? cb.conjunction()
                : cb.equal(
                from.get("appointmentDate"),
                appointmentDate
        );
    }

    public static PredicateSpecification<Appointment> hasReason(
            String reason) {
        return (from, cb) -> reason == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("reason")),
                "%" + reason.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Appointment> hasStatus(
            AppointmentStatus status) {
        return (from, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(
                from.get("status"),
                status
        );
    }

    public static PredicateSpecification<Appointment> hasPetExternalId(
            UUID petExternalId) {
        return (from, cb) -> petExternalId == null
                ? cb.conjunction()
                : cb.equal(
                from.get("pet").get("externalId"),
                petExternalId
        );
    }

    public static PredicateSpecification<Appointment> hasVeterinarianExternalId(
            UUID veterinarianExternalId) {
        return (from, cb) -> veterinarianExternalId == null
                ? cb.conjunction()
                : cb.equal(
                from.get("veterinarian").get("externalId"),
                veterinarianExternalId
        );
    }

    public static PredicateSpecification<Appointment> hasPetExternalIdIn(
            List<UUID> petExternalIds) {
        return (from, cb) -> petExternalIds == null
                ? cb.conjunction()
                : petExternalIds.isEmpty()
                ? cb.disjunction()
                : from.get("pet").get("externalId").in(petExternalIds);
    }

    public static PredicateSpecification<Appointment> hasAppointmentDateBetween(
            LocalDate dateFrom,
            LocalDate dateTo) {
        return (from, cb) -> {
            if (dateFrom == null && dateTo == null) {
                return cb.conjunction();
            }
            if (dateFrom != null && dateTo != null) {
                LocalDateTime fromDateTime = dateFrom.atStartOfDay();
                LocalDateTime toDateTime = dateTo.plusDays(1).atStartOfDay();
                return cb.and(
                        cb.greaterThanOrEqualTo(from.get("appointmentDate"), fromDateTime),
                        cb.lessThan(from.get("appointmentDate"), toDateTime)
                );
            }
            if (dateFrom != null) {
                LocalDateTime fromDateTime = dateFrom.atStartOfDay();
                return cb.greaterThanOrEqualTo(from.get("appointmentDate"), fromDateTime);
            }
            LocalDateTime toDateTime = dateTo.plusDays(1).atStartOfDay();
            return cb.lessThan(from.get("appointmentDate"), toDateTime);
        };
    }
}
