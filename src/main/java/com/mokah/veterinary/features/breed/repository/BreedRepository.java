package com.mokah.veterinary.features.breed.repository;

import com.mokah.veterinary.features.breed.entity.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreedRepository extends JpaRepository<BreedEntity, Long> {
}
