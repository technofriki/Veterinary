package com.mokah.veterinary.features.conditionsbypet.repository;

import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionByPetRepository extends JpaRepository<ConditionByPet, Long> {
}
