package com.mokah.veterinary.features.animaltypes.repository;

import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

    Optional<AnimalType> findByExternalId(UUID externalId);
}
