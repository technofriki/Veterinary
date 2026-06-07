package com.mokah.veterinary.features.prescriptions.specification;

import com.mokah.veterinary.features.prescriptions.model.Prescription;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class PrescriptionSpecification {
    public static PredicateSpecification<Prescription> hasPetId(Long petId) {
        return (root, cb) -> petId == null
                ? cb.conjunction()
                : cb.equal(
                root.get("diagnosis")
                        .get("visit")
                        .get("appointment")
                        .get("pet")
                        .get("id"),
                petId
        );
    }

    public static PredicateSpecification<Prescription> hasDiagnosisId(Long diagnosisId) {
        return (from, cb) -> diagnosisId == null
                ? cb.conjunction()
                : cb.equal(from.get("diagnosis").get("id"), diagnosisId);
    }
}
