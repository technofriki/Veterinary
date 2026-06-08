package com.mokah.veterinary.features.pets.repository;

import com.mokah.veterinary.features.pets.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByExternalId(UUID externalId);
    Optional<Pet> findByNameIgnoreCase(String name);
}
