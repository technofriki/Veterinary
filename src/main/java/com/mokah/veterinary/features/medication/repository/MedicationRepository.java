package com.mokah.veterinary.features.medication.repository;

import com.mokah.veterinary.features.medication.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<MedicationEntity, Long> {

}
