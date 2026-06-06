package com.mokah.veterinary.features.visits.specification;

import com.mokah.veterinary.features.visits.entity.VisitEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class VisitSpecification {
    public static PredicateSpecification<VisitEntity> hasId(Long id) {
        return(root, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("id"), id);
    }
    public static PredicateSpecification<VisitEntity> hasVeterinarianName(String name) {
        return (root, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + name.toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(
                                    root.get("veterinarian").get("firstName")
                            ),
                            pattern
                    ),
                    cb.like(
                            cb.lower(
                                    root.get("veterinarian").get("lastName")
                            ),
                            pattern
                    )
            );
        };
    }
    public static PredicateSpecification<VisitEntity> hasPetName(String petName) {
        return(root, cb) -> petName == null || petName.isBlank()
                ? cb.conjunction()
                : cb.like(
                cb.lower(
                        root.get("appointment")
                                .get("pet")
                                .get("name")
                ),
                "%" + petName.toLowerCase() + "%"
        );
    }
}
