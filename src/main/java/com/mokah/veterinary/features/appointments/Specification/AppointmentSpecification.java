package com.mokah.veterinary.features.appointments.Specification;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;

public class AppointmentSpecification {

    public static PredicateSpecification<Appointment> hasDate(LocalDateTime date) {
        return (root, cb) -> date == null
                ? cb.conjunction()
                : cb.equal(root.get("appointmentDate"), date);
    }

    public static PredicateSpecification<Appointment> hasReason(String reason) {
        return (root, cb) -> reason == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("reason")), "%" + reason.toLowerCase() + "%");
    }

    public static PredicateSpecification<Appointment> hasStatus(AppointmentStatus status) {
        return (root, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static PredicateSpecification<Appointment> hasPetId(Long petId) {
        return (root, cb) -> petId == null
                ? cb.conjunction()
                : cb.equal(root.get("pet").get("id"), petId);
    }

    public static PredicateSpecification<Appointment> hasVeterinarianId(Long id) {
        return (root, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("veterinarian").get("id"), id);
    }
}