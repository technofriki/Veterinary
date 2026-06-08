package com.mokah.veterinary.features.studiesbyvisit.model;

import com.mokah.veterinary.features.studies.model.Study;
import com.mokah.veterinary.features.visits.model.Visit;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "study_by_visit", uniqueConstraints = {@UniqueConstraint(columnNames = {"study_id", "visit_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyByVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "external_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID externalId;

    @PrePersist
    public void generateExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
