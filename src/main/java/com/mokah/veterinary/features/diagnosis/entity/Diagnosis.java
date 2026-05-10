package com.mokah.veterinary.features.diagnosis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "date_diagnosis", nullable = false)
    private LocalDateTime dateDiagnostic;

    @Column(name = "observations", length = 2000)
    private String observations;

}
