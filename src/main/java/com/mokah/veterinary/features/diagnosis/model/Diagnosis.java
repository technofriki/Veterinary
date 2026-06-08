package com.mokah.veterinary.features.diagnosis.model;

import com.mokah.veterinary.features.visits.model.Visit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

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

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date_diagnosis", nullable = false)
    private LocalDateTime dateDiagnosis;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;
}
