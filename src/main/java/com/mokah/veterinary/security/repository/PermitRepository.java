package com.mokah.veterinary.security.repository;

import com.mokah.veterinary.security.enums.Permits;
import com.mokah.veterinary.security.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {
    Optional<Permit> findByPermit(Permits permit);
}
