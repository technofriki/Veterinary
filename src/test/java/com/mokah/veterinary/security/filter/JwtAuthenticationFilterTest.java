package com.mokah.veterinary.security.filter;

import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.security.config.RestAuthenticationEntryPoint;
import com.mokah.veterinary.security.enums.Permits;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Permit;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private final CredentialsRepository repository = mock(CredentialsRepository.class);
    private final JwtService jwtService = new JwtService();
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repository,
            new RestAuthenticationEntryPoint());
    private final FilterChain chain = mock(FilterChain.class);
    private Credentials credentials;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", "test-secret-key-long-enough-for-hmac-sha-256");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 900_000L);
        User user = User.builder().userState(UserState.ACTIVE).build();
        Role vet = Role.builder().role(Roles.ROLE_VETERINARIAN)
                .permits(Set.of(Permit.builder().permit(Permits.VIEW_USERS).build())).build();
        Role client = Role.builder().role(Roles.ROLE_CLIENT)
                .permits(Set.of(Permit.builder().permit(Permits.VIEW_USERS).build())).build();
        credentials = Credentials.builder().username("vet@example.com").user(user).enabled(true)
                .roles(new HashSet<>(Set.of(client, vet))).build();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletResponse request(String path, String token) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }

    private Set<String> authorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(auth -> auth.getAuthority()).collect(java.util.stream.Collectors.toSet());
    }

    @Test
    void oldJwtLosesRemovedRoleButRetainsPermitSharedByOtherRoleAndRegainsRoleAfterReactivation() throws Exception {
        String oldToken = jwtService.generateToken(credentials);
        when(repository.findByUsername("vet@example.com")).thenReturn(Optional.of(credentials));
        Role vet = credentials.getRoles().stream().filter(r -> r.getRole() == Roles.ROLE_VETERINARIAN).findFirst().orElseThrow();
        credentials.getRoles().remove(vet);

        assertThat(request("/api/veterinarians", oldToken).getStatus()).isEqualTo(200);
        assertThat(authorities()).contains(Roles.ROLE_CLIENT.name(), Permits.VIEW_USERS.name())
                .doesNotContain(Roles.ROLE_VETERINARIAN.name());
        SecurityContextHolder.clearContext();

        credentials.getRoles().add(vet);
        assertThat(request("/api/veterinarians", oldToken).getStatus()).isEqualTo(200);
        assertThat(authorities()).contains(Roles.ROLE_VETERINARIAN.name());
        verify(repository, times(2)).findByUsername("vet@example.com");
        verify(chain, times(2)).doFilter(any(), any());
    }

    @Test
    void oldJwtCannotAccessProtectedRouteAfterUserDeactivationOrCredentialsDisablement() throws Exception {
        String token = jwtService.generateToken(credentials);
        when(repository.findByUsername("vet@example.com")).thenReturn(Optional.of(credentials));
        credentials.getUser().setUserState(UserState.INACTIVE);
        assertThat(request("/api/veterinarians", token).getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        credentials.getUser().setUserState(UserState.ACTIVE);
        credentials.setEnabled(false);
        assertThat(request("/api/veterinarians", token).getStatus()).isEqualTo(401);
        verifyNoInteractions(chain);
    }

    @Test
    void publicLoginStillWorksWithOldDisabledAccountTokenAndInvalidSignatureIsRejected() throws Exception {
        String token = jwtService.generateToken(credentials);
        when(repository.findByUsername("vet@example.com")).thenReturn(Optional.of(credentials));
        credentials.setEnabled(false);
        assertThat(request("/api/auth/login", token).getStatus()).isEqualTo(200);
        verify(chain).doFilter(any(), any());
        assertThat(request("/api/veterinarians", token + "corrupted").getStatus()).isEqualTo(401);
    }
}
