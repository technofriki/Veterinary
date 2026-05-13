package com.mokah.veterinary.features.owners.repository;

import com.mokah.veterinary.features.owners.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByDni(String dni);
    boolean existsByDni(String dni);
}
