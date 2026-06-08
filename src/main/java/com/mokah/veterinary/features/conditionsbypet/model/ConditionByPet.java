package com.mokah.veterinary.features.conditionsbypet.model;

import com.mokah.veterinary.features.conditions.entity.Condition;
import com.mokah.veterinary.features.pets.model.Pet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "condition_by_pet", uniqueConstraints = {@UniqueConstraint(columnNames = {"condition_id", "pet_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConditionByPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @PrePersist
    public void generateExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", nullable = false)
    private Condition condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "diagnosis_date", nullable = false)
    private LocalDateTime diagnosisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private ConditionSeverity severity;

    @Column(name = "observations")
    private String observations;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
