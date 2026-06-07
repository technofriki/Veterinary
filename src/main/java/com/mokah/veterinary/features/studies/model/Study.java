package com.mokah.veterinary.features.studies.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "studies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

}
