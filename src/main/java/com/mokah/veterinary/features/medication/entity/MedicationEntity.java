package com.mokah.veterinary.features.medication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "dosage", nullable = false)
    private Double dosage;

    @Column(name = "dosage_unit", nullable = false)
    private String dosageUnit;
}
