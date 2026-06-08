package com.mokah.veterinary.security.config;

import org.springframework.boot.CommandLineRunner;

import com.mokah.veterinary.features.users.entity.User;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.enums.Permits;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Permit;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.PermitRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityDataLoader implements CommandLineRunner {

    private final PermitRepository permitRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        loadPermits();
        loadRoles();
        createAdminUser();
    }

    private void loadPermits() {

        for (Permits permitEnum : Permits.values()) {

            permitRepository.findByPermit(permitEnum)
                    .orElseGet(() ->
                            permitRepository.save(
                                    Permit.builder()
                                            .permit(permitEnum)
                                            .build()
                            )
                    );
        }
    }

    private void loadRoles() {

        Role adminRole = roleRepository.findByRole(Roles.ROLE_ADMIN)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .role(Roles.ROLE_ADMIN)
                                        .build()
                        )
                );

        adminRole.setPermits(
                new HashSet<>(permitRepository.findAll())
        );

        roleRepository.save(adminRole);

        roleRepository.findByRole(Roles.ROLE_VETERINARIAN)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .role(Roles.ROLE_VETERINARIAN)
                                        .build()
                        )
                );

        roleRepository.findByRole(Roles.ROLE_RECEPTIONIST)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .role(Roles.ROLE_RECEPTIONIST)
                                        .build()
                        )
                );

        roleRepository.findByRole(Roles.ROLE_CLIENT)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .role(Roles.ROLE_CLIENT)
                                        .build()
                        )
                );
    }

    private void createAdminUser() {

        if (credentialsRepository.findByUsername("admin").isPresent()) {
            return;
        }

        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("System")
                .email("admin@veterinary.com")
                .userState(UserState.ACTIVE)
                .build();

        adminUser = userRepository.save(adminUser);

        Role adminRole = roleRepository.findByRole(Roles.ROLE_ADMIN)
                .orElseThrow();

        Credentials credentials = Credentials.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .enabled(true)
                .refreshToken(UUID.randomUUID().toString())
                .user(adminUser)
                .roles(Set.of(adminRole))
                .build();

        credentialsRepository.save(credentials);
    }
}
