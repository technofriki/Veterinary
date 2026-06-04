package com.mokah.veterinary.features.pets.repository;

import com.mokah.veterinary.features.pets.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
