package com.mokah.veterinary.security;

import com.mokah.veterinary.common.exception.GlobalExceptionHandler;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.controller.VeterinarianController;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(ProfessionalReactivationAuthorizationTest.Config.class)
@TestPropertySource(properties = {"jwt.secret=test-secret-key-long-enough-for-hmac-sha-256", "jwt.expiration=900000", "jwt.refresh-expiration=86400000"})
class VeterinarianMeTest {
    @Autowired VeterinarianController controller;
    @Autowired CredentialsRepository credentialsRepository;
    @Autowired VeterinarianRepository veterinarianRepository;

    private MockMvc mvc() {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private void authenticate(String username, String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority(role))));
    }

    @AfterEach void cleanup() {
        SecurityContextHolder.clearContext();
        reset(credentialsRepository, veterinarianRepository);
    }

    @Test void resolvesOwnVeterinarianFromAuthenticatedUsernameAndRoutesMeBeforeUuid() throws Exception {
        authenticate("vet@veterinary.com", "ROLE_VETERINARIAN");
        User ownUser = User.builder().externalId(UUID.randomUUID()).email("vet@veterinary.com").build();
        UUID vetId = UUID.randomUUID();
        when(credentialsRepository.findByUsername("vet@veterinary.com"))
                .thenReturn(Optional.of(Credentials.builder().user(ownUser).build()));
        when(veterinarianRepository.findByUser(ownUser))
                .thenReturn(Optional.of(Veterinarian.builder().externalId(vetId).user(ownUser).build()));

        mvc().perform(get("/api/veterinarians/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(vetId.toString()))
                .andExpect(jsonPath("$.*", hasSize(1)));
        verify(veterinarianRepository).findByUser(ownUser);
        verify(veterinarianRepository, never()).findByExternalId(any());
    }

    @Test void rejectsOtherRolesWithoutLookingUpAnIdentity() throws Exception {
        for (String role : List.of("ROLE_CLIENT", "ROLE_RECEPTIONIST", "ROLE_ADMIN")) {
            authenticate("someone@veterinary.com", role);
            mvc().perform(get("/api/veterinarians/me"))
                    .andExpect(status().isForbidden());
        }
        verifyNoInteractions(credentialsRepository, veterinarianRepository);
    }

    @Test void returnsControlledNotFoundWhenAuthenticatedUserHasNoVeterinarian() throws Exception {
        authenticate("vet@veterinary.com", "ROLE_VETERINARIAN");
        User ownUser = User.builder().externalId(UUID.randomUUID()).build();
        when(credentialsRepository.findByUsername("vet@veterinary.com"))
                .thenReturn(Optional.of(Credentials.builder().user(ownUser).build()));
        when(veterinarianRepository.findByUser(ownUser)).thenReturn(Optional.empty());

        mvc().perform(get("/api/veterinarians/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Veterinarian associated with authenticated user not found"));
    }
}
