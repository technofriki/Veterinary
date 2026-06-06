package com.mokah.veterinary.features.diagnosisbystudies.repository;

import com.mokah.veterinary.features.diagnosisbystudies.model.DiagnosisByStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisByStudyRepository extends JpaRepository<DiagnosisByStudy, Long> {
    boolean existsByDiagnosisIdAndStudyId(Long diagnosisId, Long studyId);
}
