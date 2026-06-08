package com.mokah.veterinary.features.diagnosisbystudies.model;

import com.mokah.veterinary.features.diagnosis.entity.Diagnosis;
import com.mokah.veterinary.features.studies.model.Study;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "diagnosis_by_study", uniqueConstraints = {@UniqueConstraint(columnNames = {"diagnosis_id", "study_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiagnosisByStudy {

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
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "study_conclusions")
    private String studyConclusions;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
