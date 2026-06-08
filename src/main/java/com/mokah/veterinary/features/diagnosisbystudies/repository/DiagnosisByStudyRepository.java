package com.mokah.veterinary.features.diagnosisbystudies.repository;

import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiagnosisByStudyRepository extends JpaRepository<DiagnosisByStudy, Long> {

    boolean existsByDiagnosisIdAndStudyId(
            Long diagnosisId,
            Long studyId
    );

    Optional<DiagnosisByStudy> findByExternalId(UUID externalId);
}
