package com.mokah.veterinary.security.repository;

import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(Roles role);

}
