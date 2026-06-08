package com.mokah.veterinary.features.diagnosis.repository;

import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByExternalId(UUID externalId);
}
