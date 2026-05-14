package com.mokah.veterinary.features.pets.repository;

import com.mokah.veterinary.features.pets.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
}
