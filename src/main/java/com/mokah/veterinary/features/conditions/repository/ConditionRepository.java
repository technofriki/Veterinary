package com.mokah.veterinary.features.conditions.repository;

import com.mokah.veterinary.features.conditions.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {

    Optional<Condition> findByExternalId(UUID externalId);
}
