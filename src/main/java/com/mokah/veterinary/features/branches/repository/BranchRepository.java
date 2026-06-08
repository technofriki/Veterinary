package com.mokah.veterinary.features.branches.repository;

import com.mokah.veterinary.features.branches.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByExternalId(UUID externalId);
    Optional<Branch> findByName(String name);
    boolean existsByName(String name);
}
