package com.mokah.veterinary.features.visits.model;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appoinment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private Veterinarian  veterinarian;
}
