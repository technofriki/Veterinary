package com.mokah.veterinary.features.veterinarians.model;

import com.mokah.veterinary.features.branches.entity.Branch;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veterinarians")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

}
