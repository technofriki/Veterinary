package com.mokah.veterinary.features.veterinarians.specification;

import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class VeterinarianSpecification {

    public static PredicateSpecification<Veterinarian> hasFirstName(String firstName) {
        return (from, cb) -> firstName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("firstName")),
                "%" + firstName.toLowerCase() + "%");
    }

    public static PredicateSpecification<Veterinarian> hasLastName(String lastName) {
        return (from, cb) -> lastName == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("lastName")),
                "%" + lastName.toLowerCase() + "%");
    }

    public static PredicateSpecification<Veterinarian> hasLicenseNumber(String licenseNumber) {
        return (from, cb) -> licenseNumber == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("licenseNumber")),
                "%" + licenseNumber.toLowerCase() + "%");
    }

    public static PredicateSpecification<Veterinarian> hasBranchId(Long branchId) {
        return (from, cb) -> branchId == null
                ? cb.conjunction()
                : cb.equal(
                from.get("branch").get("id"),
                branchId);
    }
}
