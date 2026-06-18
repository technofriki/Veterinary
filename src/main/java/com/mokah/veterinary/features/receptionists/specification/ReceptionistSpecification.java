package com.mokah.veterinary.features.receptionists.specification;

import com.mokah.veterinary.features.receptionists.model.Receptionist;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.UUID;

public class ReceptionistSpecification {

    public static PredicateSpecification<Receptionist> hasFirstName(String firstName) {
        return (root, cb) -> firstName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(root.join("user").get("firstName")),
                "%" + firstName.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Receptionist> hasLastName(String lastName) {
        return (root, cb) -> lastName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(root.join("user").get("lastName")),
                "%" + lastName.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Receptionist> hasBranchExternalId(UUID branchExternalId) {
        return (root, cb) -> branchExternalId == null
                ? cb.conjunction()
                : cb.equal(
                root.join("branch").get("externalId"),
                branchExternalId
        );
    }
}
