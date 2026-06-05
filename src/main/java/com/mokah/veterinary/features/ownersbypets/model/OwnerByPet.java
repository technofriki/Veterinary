package com.mokah.veterinary.features.ownersbypets.model;

import com.mokah.veterinary.features.owners.entity.Owner;
import com.mokah.veterinary.features.pets.model.Pet;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
