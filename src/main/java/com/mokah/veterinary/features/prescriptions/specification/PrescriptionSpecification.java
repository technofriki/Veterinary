package com.mokah.veterinary.features.prescriptions.specification;

import com.mokah.veterinary.features.prescriptions.model.Prescription;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.UUID;

public class PrescriptionSpecification {

    public static PredicateSpecification<Prescription> hasDiagnosisExternalId(UUID externalId) {
        return (root, cb) -> externalId == null
                ? cb.conjunction()
                : cb.equal(
                root.get("diagnosis")
                        .get("externalId"),
                externalId
        );
    }

    public static PredicateSpecification<Prescription> hasPetExternalId(UUID externalId) {
        return (root, cb) -> externalId == null
                ? cb.conjunction()
                : cb.equal(
                root.get("diagnosis")
                        .get("visit")
                        .get("appointment")
                        .get("pet")
                        .get("externalId"),
                externalId
        );
    }
}
