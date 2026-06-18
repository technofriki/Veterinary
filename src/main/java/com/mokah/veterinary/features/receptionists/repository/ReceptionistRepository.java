package com.mokah.veterinary.features.receptionists.repository;

import com.mokah.veterinary.features.receptionists.model.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, Long>, JpaSpecificationExecutor<Receptionist> {
    Boolean existsByPhone(String phone);
    Optional<Receptionist> findByExternalId(UUID externalId);
}
