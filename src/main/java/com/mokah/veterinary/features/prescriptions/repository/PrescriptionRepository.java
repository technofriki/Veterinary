package com.mokah.veterinary.features.prescriptions.repository;

import com.mokah.veterinary.features.prescriptions.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long>, JpaSpecificationExecutor<Prescription> {
    List<Prescription> findByDiagnosisId(Long diagnosisId);
    List<Prescription> findByDiagnosisVisitAppointmentPetId(Long petId);
}
