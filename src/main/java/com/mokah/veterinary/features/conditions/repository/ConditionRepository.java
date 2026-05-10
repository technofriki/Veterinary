package com.mokah.veterinary.features.conditions.repository;

import com.mokah.veterinary.features.conditions.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
