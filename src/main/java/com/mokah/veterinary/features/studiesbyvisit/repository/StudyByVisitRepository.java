package com.mokah.veterinary.features.studiesbyvisit.repository;

import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyByVisitRepository
        extends JpaRepository<StudyByVisit, Long> {

    Optional<StudyByVisit> findByExternalId(UUID externalId);

    boolean existsByStudyExternalIdAndVisitExternalId(
            UUID studyExternalId,
            UUID visitExternalId
    );
}
