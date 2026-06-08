package com.mokah.veterinary.features.medication.repository;

import com.mokah.veterinary.features.medication.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

}
