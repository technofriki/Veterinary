package com.mokah.veterinary.features.ownersbypets.model;

import com.mokah.veterinary.features.owners.entity.Owner;
import com.mokah.veterinary.features.pets.model.Pet;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "owner_by_pet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerByPet {

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
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
