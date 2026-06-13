package com.mokah.veterinary.features.visits.specification;

import com.mokah.veterinary.features.visits.model.Visit;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.UUID;

public class VisitSpecification {

    public static PredicateSpecification<Visit> hasExternalId(UUID externalId) {
        return (from, cb) -> externalId == null
                ? cb.conjunction()
                : cb.equal(from.get("externalId"), externalId);
    }

    public static PredicateSpecification<Visit> hasVeterinarianName(String veterinarianName) {
        return (from, cb) -> {

            if (veterinarianName == null || veterinarianName.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + veterinarianName.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(from.get("veterinarian").get("firstName")), pattern),
                    cb.like(cb.lower(from.get("veterinarian").get("lastName")), pattern)
            );
        };
    }

    public static PredicateSpecification<Visit> hasPetName(String petName) {
        return (from, cb) -> {

            if (petName == null || petName.isBlank()) {
                return cb.conjunction();
            }

            return cb.and(
                    cb.isNotNull(from.get("appointment")),
                    cb.like(cb.lower(from.get("appointment").get("pet").get("name")), "%" + petName.toLowerCase() + "%")
            );
        };
    }

    public static PredicateSpecification<Visit> isWalkIn(Boolean walkIn) {
        return (from, cb) -> {

            if (walkIn == null) {
                return cb.conjunction();
            }

            if (walkIn) {
                return cb.isNull(from.get("appointment"));
            }

            return cb.isNotNull(from.get("appointment"));
        };
    }
}