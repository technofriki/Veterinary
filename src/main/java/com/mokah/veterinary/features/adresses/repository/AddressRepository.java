package com.mokah.veterinary.features.adresses.repository;

import com.mokah.veterinary.features.adresses.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
