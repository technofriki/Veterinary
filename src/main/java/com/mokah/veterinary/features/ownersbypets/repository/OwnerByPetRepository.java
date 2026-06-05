package com.mokah.veterinary.features.ownersbypets.repository;

import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerByPetRepository extends JpaRepository<OwnerByPet, Long> {

    boolean existsByOwnerIdAndPetId(Long ownerId, Long petId);

    boolean existsByOwnerIdAndPetIdAndIdNot(
            Long ownerId,
            Long petId,
            Long id
    );
}
