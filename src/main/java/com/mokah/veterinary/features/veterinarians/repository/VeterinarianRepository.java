package com.mokah.veterinary.features.veterinarians.repository;

import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long>, JpaSpecificationExecutor<Veterinarian> {
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Boolean existsByLicenseNumber(String licenseNumber);
}
