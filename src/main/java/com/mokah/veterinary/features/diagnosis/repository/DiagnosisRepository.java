package com.mokah.veterinary.features.diagnosis.repository;

import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

}
