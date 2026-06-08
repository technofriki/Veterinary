package com.mokah.veterinary.features.medication.repository;

import com.mokah.veterinary.features.medication.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    Optional<Medication> findByExternalId(UUID externalId);
    Optional<Medication> findByNameIgnoreCase(String name);
}
