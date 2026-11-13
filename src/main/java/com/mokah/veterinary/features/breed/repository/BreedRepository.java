package com.mokah.veterinary.features.breed.repository;

import com.mokah.veterinary.features.breed.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
    Optional<Breed> findByExternalId(UUID externalId);
    Optional<Breed> findByNameIgnoreCase(String name);

    List<Breed> findByAnimalType_ExternalId(UUID animalTypeId);

    boolean existsByNameIgnoreCaseAndAnimalType_ExternalId(String name, UUID animalTypeId);

    boolean existsByNameIgnoreCaseAndAnimalType_ExternalIdAndExternalIdNot(
            String name, UUID animalTypeId, UUID excludeId);
}
