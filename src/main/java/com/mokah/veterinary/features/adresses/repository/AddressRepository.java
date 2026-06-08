package com.mokah.veterinary.features.adresses.repository;

import com.mokah.veterinary.features.adresses.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
