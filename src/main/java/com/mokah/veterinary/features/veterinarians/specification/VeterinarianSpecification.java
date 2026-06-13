package com.mokah.veterinary.features.veterinarians.specification;

import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.UUID;

public class VeterinarianSpecification {

    public static PredicateSpecification<Veterinarian> hasFirstName(String firstName) {
        return (root, cb) -> firstName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(root.join("user").get("firstName")),
                "%" + firstName.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Veterinarian> hasLastName(String lastName) {
        return (root, cb) -> lastName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(root.join("user").get("lastName")),
                "%" + lastName.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Veterinarian> hasLicenseNumber(String licenseNumber) {
        return (root, cb) -> licenseNumber == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(root.get("licenseNumber")),
                "%" + licenseNumber.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Veterinarian> hasBranchExternalId(UUID branchExternalId) {
        return (root, cb) -> branchExternalId == null
                ? cb.conjunction()
                : cb.equal(
                root.join("branch").get("externalId"),
                branchExternalId
        );
    }
}