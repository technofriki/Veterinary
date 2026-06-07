package com.mokah.veterinary.features.studiesbyvisit.repository;

import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyByVisitRepository extends JpaRepository<StudyByVisit, Long> {
    boolean existsByStudyIdAndVisitId(Long studyId, Long visitId);
}
