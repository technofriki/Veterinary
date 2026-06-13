package com.mokah.veterinary.features.veterinarians.repository;

import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long>, JpaSpecificationExecutor<Veterinarian> {
    Boolean existsByUser_Email(String email);
    Boolean existsByPhone(String phone);
    Boolean existsByLicenseNumber(String licenseNumber);
    Optional<Veterinarian> findByExternalId(UUID externalId);
}
