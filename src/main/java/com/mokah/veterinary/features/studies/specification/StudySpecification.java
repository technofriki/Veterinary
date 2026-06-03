package com.mokah.veterinary.features.studies.specification;

import com.mokah.veterinary.features.studies.entity.Study;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class StudySpecification {

    public static PredicateSpecification<Study> hasName(String name) {
        return (from, cb) -> name == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("name")), "%" + name.toLowerCase() + "%"
        );
    }

    public static PredicateSpecification<Study> hasDescription(String description) {
        return (from, cb) -> description == null
                ? cb.conjunction()
                : cb.like(
                cb.lower(from.get("description")), "%" + description.toLowerCase() + "%"
        );
    }
}
