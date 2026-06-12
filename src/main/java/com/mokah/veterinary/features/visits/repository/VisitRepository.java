package com.mokah.veterinary.features.visits.repository;

import com.mokah.veterinary.features.visits.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    Optional<Visit> findByExternalId(UUID externalId);
    List<Visit> findByAppointment_Pet_ExternalId(UUID petExternalId);
    boolean existsByAppointment_ExternalId(UUID appointmentExternalId);
}
