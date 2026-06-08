package com.mokah.veterinary.features.owners.repository;

import com.mokah.veterinary.features.owners.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByExternalId(UUID externalId);
    Optional<Owner> findByDni(String dni);
    boolean existsByDni(String dni);
}
