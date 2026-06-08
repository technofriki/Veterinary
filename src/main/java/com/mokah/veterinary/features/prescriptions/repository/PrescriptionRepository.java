package com.mokah.veterinary.features.prescriptions.repository;

import com.mokah.veterinary.features.prescriptions.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long>, JpaSpecificationExecutor<Prescription> {
    Optional<Prescription> findByExternalId(UUID externalId);
}
