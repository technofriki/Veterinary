package com.mokah.veterinary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokah.veterinary.features.receptionists.controller.ReceptionistController;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistPublicResponse;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.receptionists.service.ReceptionistService;
import com.mokah.veterinary.features.users.controller.UserController;
import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.services.UserServiceInterface;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.controller.VeterinarianController;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianPublicResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProfessionalVisibilityTest {
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    private final UUID id = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final VeterinarianService vets = mock(VeterinarianService.class);
    private final VeterinarianRepository vetRepo = mock(VeterinarianRepository.class);
    private final ReceptionistService receptionists = mock(ReceptionistService.class);
    private final ReceptionistRepository receptionRepo = mock(ReceptionistRepository.class);
    private final UserServiceInterface users = mock(UserServiceInterface.class);
    private final VeterinarianController vetController = new VeterinarianController(vets, vetRepo);
    private final ReceptionistController receptionController = new ReceptionistController(receptionists, receptionRepo);
    private final UserController userController = new UserController(users);

    private void login(String role, String username) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority(role))));
    }

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void adminReceivesAdministrativeListAndDetail() throws Exception {
        login("ROLE_ADMIN", "admin");
        var fullVet = new VeterinarianResponse(id, "Ana", "Lopez", "MAT-1", "123", "ana@vet.com",
                null, null, Set.of(DayOfWeek.MONDAY), true, null);
        var fullReception = new ReceptionistResponse(id, "Ana", "Lopez", "123", "ana@vet.com", true, null);
        when(vets.findAll(null, null, null, null)).thenReturn(List.of(fullVet));
        when(vets.findById(id)).thenReturn(fullVet);
        when(receptionists.findAll(null, null, null)).thenReturn(List.of(fullReception));
        when(receptionists.findById(id)).thenReturn(fullReception);
        assertThat(json.writeValueAsString(vetController.findAll(null, null, null, null))).contains("licenseNumber", "phone", "email", "active");
        assertThat(json.writeValueAsString(vetController.findById(id))).contains("licenseNumber", "phone", "email", "active");
        assertThat(json.writeValueAsString(receptionController.findAll(null, null, null))).contains("phone", "email", "active");
        assertThat(json.writeValueAsString(receptionController.findById(id))).contains("phone", "email", "active");
        when(users.findByExternalId(userId)).thenReturn(new UserResponse(userId, "Ana", "Lopez", "ana@vet.com", null));
        assertThat(json.writeValueAsString(userController.getUserByExternalId(userId).getBody())).contains("email", "userState");
    }

    @Test void everyNonAdminRoleGetsOnlyPublicProfessionalFieldsOnSearchAndDirectLookup() throws Exception {
        var publicVet = new VeterinarianPublicResponse(id, "Ana", "Lopez", LocalTime.of(9, 0),
                LocalTime.of(17, 0), Set.of(DayOfWeek.MONDAY));
        var publicReception = new ReceptionistPublicResponse(id, "Ana", "Lopez");
        when(vets.findAllPublic(null, null, null, null)).thenReturn(List.of(publicVet));
        when(vets.findPublicById(id)).thenReturn(publicVet);
        when(receptionists.findAllPublic(null, null, null)).thenReturn(List.of(publicReception));
        when(receptionists.findPublicById(id)).thenReturn(publicReception);
        for (String role : List.of("ROLE_CLIENT", "ROLE_RECEPTIONIST", "ROLE_VETERINARIAN")) {
            login(role, "someone-else@vet.com");
            for (Object result : List.of(vetController.findAll(null, null, null, null), vetController.findById(id),
                    receptionController.findAll(null, null, null), receptionController.findById(id))) {
                String body = json.writeValueAsString(result);
                assertThat(body).contains("externalId", "firstName", "lastName")
                        .doesNotContain("licenseNumber", "phone", "email", "active", "userState");
            }
        }
    }

    @Test void ownProfileRemainsAvailableButOtherUsersArePublic() throws Exception {
        User user = User.builder().externalId(userId).email("own@vet.com").build();
        when(vetRepo.findByExternalId(id)).thenReturn(Optional.of(Veterinarian.builder().user(user).build()));
        when(receptionRepo.findByExternalId(id)).thenReturn(Optional.of(Receptionist.builder().user(user).build()));
        when(vets.findById(id)).thenReturn(new VeterinarianResponse(id, "Ana", "Lopez", "MAT-1", "123", "own@vet.com", null, null, null, true, null));
        when(receptionists.findById(id)).thenReturn(new ReceptionistResponse(id, "Ana", "Lopez", "123", "own@vet.com", true, null));
        UserResponse full = new UserResponse(userId, "Ana", "Lopez", "own@vet.com", null);
        when(users.findByExternalId(userId)).thenReturn(full);
        when(users.findAll()).thenReturn(List.of(full));
        login("ROLE_VETERINARIAN", "own@vet.com");
        assertThat(json.writeValueAsString(vetController.findById(id))).contains("MAT-1");
        assertThat(json.writeValueAsString(receptionController.findById(id))).contains("phone");
        assertThat(json.writeValueAsString(userController.getUserByExternalId(userId).getBody())).contains("email");
        login("ROLE_RECEPTIONIST", "other@vet.com");
        assertThat(json.writeValueAsString(userController.getUserByExternalId(userId).getBody())).doesNotContain("email", "userState");
        assertThat(json.writeValueAsString(userController.findAllUsers().getBody())).doesNotContain("email", "userState");
    }

    @Test void nestedProfessionalMappingUsesTheSameServerSideAuthority() throws Exception {
        User user = User.builder().firstName("Ana").lastName("Lopez").email("private@vet.com").build();
        Veterinarian vet = Veterinarian.builder().externalId(id).user(user).phone("123")
                .licenseNumber("MAT-1").active(true).build();
        VeterinarianMapper mapper = Mappers.getMapper(VeterinarianMapper.class);
        login("ROLE_CLIENT", "client");
        assertThat(json.writeValueAsString(mapper.toVisibleResponse(vet)))
                .contains("externalId", "firstName").doesNotContain("phone", "email", "licenseNumber", "active");
        login("ROLE_ADMIN", "admin");
        assertThat(json.writeValueAsString(mapper.toVisibleResponse(vet)))
                .contains("phone", "email", "licenseNumber", "active");
    }

    @Test void selectorsKeepExistingFiltersAndProfessionalIdentifiers() {
        UUID branchId = UUID.randomUUID();
        login("ROLE_CLIENT", "client");
        var publicVet = new VeterinarianPublicResponse(id, "Ana", "Lopez", null, null, Set.of());
        var publicReception = new ReceptionistPublicResponse(id, "Ana", "Lopez");
        when(vets.findAllPublic("Ana", "Lopez", null, branchId)).thenReturn(List.of(publicVet));
        when(receptionists.findAllPublic("Ana", "Lopez", branchId)).thenReturn(List.of(publicReception));
        assertThat(vetController.findAll("Ana", "Lopez", null, branchId)).isEqualTo(List.of(publicVet));
        assertThatThrownBy(() -> vetController.findAll("Ana", "Lopez", "MAT-1", branchId))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(receptionController.findAll("Ana", "Lopez", branchId)).isEqualTo(List.of(publicReception));
        assertThat(publicVet.externalId()).isEqualTo(id);
    }
}
