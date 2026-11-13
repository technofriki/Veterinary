package com.mokah.veterinary.security;

import com.mokah.veterinary.features.receptionists.controller.ReceptionistController;
import com.mokah.veterinary.features.receptionists.mapper.ReceptionistMapper;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.receptionists.service.ReceptionistService;
import com.mokah.veterinary.features.receptionists.service.ReceptionistServiceImpl;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.veterinarians.controller.VeterinarianController;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianServiceImpl;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import com.mokah.veterinary.security.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(ProfessionalReactivationAuthorizationTest.Config.class)
@TestPropertySource(properties = {"jwt.secret=test-secret-key-long-enough-for-hmac-sha-256", "jwt.expiration=900000", "jwt.refresh-expiration=86400000"})
class ProfessionalReactivationAuthorizationTest {
    @Configuration
    @EnableMethodSecurity
    static class Config {
        @Bean VeterinarianRepository vets() { return mock(VeterinarianRepository.class); }
        @Bean ReceptionistRepository receptionists() { return mock(ReceptionistRepository.class); }
        @Bean VeterinarianMapper vetMapper() { return mock(VeterinarianMapper.class); }
        @Bean ReceptionistMapper receptionistMapper() { return mock(ReceptionistMapper.class); }
        @Bean BranchService branches() { return mock(BranchService.class); }
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean CredentialsRepository credentials() { return mock(CredentialsRepository.class); }
        @Bean RoleRepository roles() { return mock(RoleRepository.class); }
        @Bean PasswordEncoder passwordEncoder() { return mock(PasswordEncoder.class); }
        @Bean JwtService jwtService() { return mock(JwtService.class); }
        @Bean VeterinarianService vetService(VeterinarianRepository repo, VeterinarianMapper mapper,
                BranchService branches, UserRepository users, CredentialsRepository credentials,
                RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
            return new VeterinarianServiceImpl(repo, mapper, branches, users, credentials, roles, encoder, jwt);
        }
        @Bean ReceptionistService receptionistService(ReceptionistRepository repo, ReceptionistMapper mapper,
                BranchService branches, UserRepository users, CredentialsRepository credentials,
                RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
            return new ReceptionistServiceImpl(repo, mapper, branches, users, credentials, roles, encoder, jwt);
        }
        @Bean VeterinarianController vetController(VeterinarianService service, VeterinarianRepository repo) { return new VeterinarianController(service, repo); }
        @Bean ReceptionistController receptionistController(ReceptionistService service, ReceptionistRepository repo) { return new ReceptionistController(service, repo); }
    }

    @Autowired VeterinarianController vetController;
    @Autowired ReceptionistController receptionistController;
    @Autowired VeterinarianService vetService;
    @Autowired ReceptionistService receptionistService;
    @Autowired VeterinarianRepository vetRepository;
    @Autowired ReceptionistRepository receptionistRepository;

    private final UUID id = UUID.randomUUID();

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        reset(vetRepository, receptionistRepository);
    }

    private void login(String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("actor", null,
                Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList()));
    }

    @Test
    void adminPassesBothControllerAndServiceGuards() {
        login("ROLE_ADMIN");
        // The not-found result proves authorization reached the service lookup.
        assertThatThrownBy(() -> vetController.reactivate(id)).isInstanceOf(com.mokah.veterinary.common.exception.ResourceNotFoundException.class);
        assertThatThrownBy(() -> receptionistController.reactivate(id)).isInstanceOf(com.mokah.veterinary.common.exception.ResourceNotFoundException.class);
        verify(vetRepository).findByExternalId(id);
        verify(receptionistRepository).findByExternalId(id);
    }

    @Test
    void updateUsersPermissionWithoutAdminRoleCannotPassControllerOrService() {
        login("ROLE_CLIENT", "UPDATE_USERS");
        assertDenied(AccessDeniedException.class);
    }

    @Test
    void anonymousCannotPassControllerOrService() {
        assertDenied(AuthenticationCredentialsNotFoundException.class);
    }

    private void assertDenied(Class<? extends RuntimeException> expected) {
        assertThatThrownBy(() -> vetController.reactivate(id)).isInstanceOf(expected);
        assertThatThrownBy(() -> receptionistController.reactivate(id)).isInstanceOf(expected);
        assertThatThrownBy(() -> vetService.reactivate(id)).isInstanceOf(expected);
        assertThatThrownBy(() -> receptionistService.reactivate(id)).isInstanceOf(expected);
        verifyNoInteractions(vetRepository, receptionistRepository);
    }
}
