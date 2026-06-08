package com.mokah.veterinary.features.users.repository;

import com.mokah.veterinary.features.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByExternalId(UUID externalId);
}
