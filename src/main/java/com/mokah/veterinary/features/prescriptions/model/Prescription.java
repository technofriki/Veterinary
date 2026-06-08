package com.mokah.veterinary.features.prescriptions.model;

import com.mokah.veterinary.features.diagnosis.model.Diagnosis;
import com.mokah.veterinary.features.medication.model.Medication;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

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

    @Column(nullable = false)
    private Long quantity;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String indication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;
}