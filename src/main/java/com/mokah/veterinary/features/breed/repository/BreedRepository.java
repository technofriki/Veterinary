package com.mokah.veterinary.features.breed.repository;

import com.mokah.veterinary.features.breed.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

    Optional<Breed> findByExternalId(UUID externalId);
}
