package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.repository.BranchRepository;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.receptionists.service.ReceptionistService;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:lifecycle;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class VeterinarianLifecycleTransactionTest {
    @Autowired VeterinarianService service;
    @Autowired ReceptionistService receptionistService;
    @Autowired ReceptionistRepository receptionists;
    @Autowired VeterinarianRepository vets;
    @Autowired CredentialsRepository credentialsRepository;
    @Autowired UserRepository users;
    @Autowired BranchRepository branches;
    @Autowired RoleRepository roles;
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void databaseFailureWhileActivatingProfileRollsBackRoleChange() {
        User user = users.save(User.builder().firstName("Vet").lastName("Rollback")
                .email("rollback-vet@example.com").userState(UserState.ACTIVE).build());
        Role otherRole = roles.findByRole(Roles.ROLE_CLIENT).orElseThrow();
        credentialsRepository.save(Credentials.builder().username("rollback-vet@example.com")
                .password("hash").enabled(true).refreshToken("rollback-refresh")
                .user(user).roles(Set.of(otherRole)).build());
        Branch branch = branches.save(Branch.builder().name("Rollback branch").phone("rb-123")
                .email("rollback-branch@example.com").openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(18, 0)).build());
        Veterinarian vet = vets.save(Veterinarian.builder().licenseNumber("RB-LIC-1").phone("rb-vet-123")
                .active(false).branch(branch).user(user).build());

        // A real database constraint forces a failure when the transaction flushes the active profile.
        jdbc.execute("ALTER TABLE veterinarians ADD CONSTRAINT inactive_only CHECK (active = false)");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null,
                Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        assertThatThrownBy(() -> service.reactivate(vet.getExternalId())).isInstanceOf(RuntimeException.class);
        assertThat(vets.findByExternalId(vet.getExternalId()).orElseThrow().getActive()).isFalse();
        assertThat(credentialsRepository.findByUser(user).orElseThrow().getRoles())
                .extracting(Role::getRole).containsExactly(Roles.ROLE_CLIENT);
    }

    @Test
    void databaseFailureWhileActivatingReceptionistRollsBackRoleChange() {
        User user = users.save(User.builder().firstName("Receptionist").lastName("Rollback")
                .email("rollback-receptionist@example.com").userState(UserState.ACTIVE).build());
        Role otherRole = roles.findByRole(Roles.ROLE_CLIENT).orElseThrow();
        credentialsRepository.save(Credentials.builder().username("rollback-receptionist@example.com")
                .password("hash").enabled(true).refreshToken("rollback-receptionist-refresh")
                .user(user).roles(Set.of(otherRole)).build());
        Branch branch = branches.save(Branch.builder().name("Receptionist rollback branch").phone("rb-456")
                .email("rb-receptionist-branch@example.com").openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(18, 0)).build());
        Receptionist receptionist = receptionists.save(Receptionist.builder().phone("rb-receptionist-123")
                .active(false).branch(branch).user(user).build());

        jdbc.execute("ALTER TABLE receptionists ADD CONSTRAINT inactive_receptionist_only CHECK (active = false)");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null,
                Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        assertThatThrownBy(() -> receptionistService.reactivate(receptionist.getExternalId()))
                .isInstanceOf(RuntimeException.class);
        assertThat(receptionists.findByExternalId(receptionist.getExternalId()).orElseThrow().getActive()).isFalse();
        assertThat(credentialsRepository.findByUser(user).orElseThrow().getRoles())
                .extracting(Role::getRole).containsExactly(Roles.ROLE_CLIENT);
    }
}
