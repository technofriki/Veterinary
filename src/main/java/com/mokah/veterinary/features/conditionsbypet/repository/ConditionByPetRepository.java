package com.mokah.veterinary.features.conditionsbypet.repository;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConditionByPetRepository
        extends JpaRepository<ConditionByPet, Long> {

    Optional<ConditionByPet> findByExternalId(UUID externalId);

    boolean existsByConditionIdAndPetId(
            Long conditionId,
            Long petId
    );
}
