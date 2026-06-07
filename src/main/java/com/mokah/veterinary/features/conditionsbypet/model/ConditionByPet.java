package com.mokah.veterinary.features.conditionsbypet.model;

import com.mokah.veterinary.features.conditions.entity.Condition;
import com.mokah.veterinary.features.pets.model.Pet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
// no permitir dos filas con la misma combinacion de condition_id y pet_id por base de datos
@Table(name = "condition_by_pet", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"condition_id", "pet_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConditionByPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id")
    private Condition condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "diagnosis_date", nullable = false)
    private LocalDateTime diagnosisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private ConditionSeverity severity;

    @Column(name = "observations")
    private String observations;

    @Column(name = "active", nullable = false)
    private Boolean active;

}
