package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianEmailExistsException;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianLicenseExistsException;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianPhoneExistsException;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapper;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.features.veterinarians.specification.VeterinarianSpecification;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import com.mokah.veterinary.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeterinarianServiceImpl implements VeterinarianService {

    private final VeterinarianRepository repository;
    private final VeterinarianMapper mapper;
    private final BranchService branchService;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public VeterinarianResponse create(VeterinarianCreateDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new VeterinarianEmailExistsException(
                    "User with email " + dto.email() + " already exists");
        }

        if (credentialsRepository.findByUsername(dto.email()).isPresent()) {
            throw new VeterinarianEmailExistsException(
                    "Username " + dto.email() + " already exists");
        }

        if (repository.existsByLicenseNumber(dto.licenseNumber())) {
            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license number " + dto.licenseNumber() + " already exists");
        }

        if (repository.existsByPhone(dto.phone())) {
            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + dto.phone() + " already exists");
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .userState(UserState.ACTIVE)
                .build();
        user = userRepository.save(user);

        Role veterinarianRole = roleRepository.findByRole(Roles.ROLE_VETERINARIAN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", Roles.ROLE_VETERINARIAN.name()));

        String encodedPassword = passwordEncoder.encode(dto.password());

        Credentials credentials = Credentials.builder()
                .username(dto.email())
                .password(encodedPassword)
                .enabled(true)
                .user(user)
                .roles(Set.of(veterinarianRole))
                .build();

        String refreshToken = jwtService.generateRefreshToken(credentials);
        credentials.setRefreshToken(refreshToken);

        credentialsRepository.save(credentials);

        Veterinarian entity = mapper.toEntity(dto);
        entity.setUser(user);

        Branch branch = branchService.entityByExternalId(dto.branchExternalId());
        entity.setBranch(branch);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Veterinarian entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian", "externalId", externalId));
    }

    @Override
    public VeterinarianResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<VeterinarianResponse> findAll(
            String firstName,
            String lastName,
            String licenseNumber,
            UUID branchExternalId) {

        PredicateSpecification<Veterinarian> spec = PredicateSpecification.allOf(
                VeterinarianSpecification.hasFirstName(firstName),
                VeterinarianSpecification.hasLastName(lastName),
                VeterinarianSpecification.hasLicenseNumber(licenseNumber),
                VeterinarianSpecification.hasBranchExternalId(branchExternalId)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    public VeterinarianResponse update(UUID externalId, VeterinarianUpdateDTO dto) {

        Veterinarian entity = entityByExternalId(externalId);

        if (dto.email() != null
                && !dto.email().equalsIgnoreCase(entity.getUser().getEmail())
                && userRepository.existsByEmail(dto.email())) {

            throw new VeterinarianEmailExistsException(
                    "User with email " + dto.email() + " already exists");
        }

        if (dto.phone() != null
                && !dto.phone().equalsIgnoreCase(entity.getPhone())
                && repository.existsByPhone(dto.phone())) {

            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + dto.phone() + " already exists");
        }

        if (dto.licenseNumber() != null
                && !dto.licenseNumber().equalsIgnoreCase(entity.getLicenseNumber())
                && repository.existsByLicenseNumber(dto.licenseNumber())) {

            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license " + dto.licenseNumber() + " already exists");
        }

        mapper.update(entity, dto);

        if (dto.firstName() != null) {
            entity.getUser().setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            entity.getUser().setLastName(dto.lastName());
        }
        if (dto.email() != null) {
            entity.getUser().setEmail(dto.email());
        }

        if (dto.branchExternalId() != null) {
            Branch branch = branchService.entityByExternalId(dto.branchExternalId());
            entity.setBranch(branch);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        Veterinarian entity = entityByExternalId(externalId);
        if (!Boolean.TRUE.equals(entity.getActive())) {
            throw new BusinessRuleException("The selected veterinarian is no longer active in the system.");
        }
        if (entity.getUser() != null) {
            credentialsRepository.findByUser(entity.getUser()).ifPresent(credentials -> {
                Set<Role> roles = new HashSet<>(credentials.getRoles() == null ? Set.of() : credentials.getRoles());
                if (roles.removeIf(role -> role.getRole() == Roles.ROLE_VETERINARIAN)) {
                    credentials.setRoles(roles);
                    credentialsRepository.save(credentials);
                }
            });
        }
        entity.setActive(false);
        repository.save(entity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public VeterinarianResponse reactivate(UUID externalId) {
        Veterinarian entity = entityByExternalId(externalId);
        if (Boolean.TRUE.equals(entity.getActive())) {
            throw new BusinessRuleException("Veterinarian is already active.");
        }
        User user = entity.getUser();
        if (user == null) {
            throw new BusinessRuleException("Veterinarian has no associated user.");
        }
        if (user.getUserState() != UserState.ACTIVE) {
            throw new BusinessRuleException("Cannot reactivate veterinarian: associated user is inactive.");
        }
        Credentials credentials = credentialsRepository.findByUser(user)
                .orElseThrow(() -> new BusinessRuleException("Veterinarian has no associated credentials."));
        if (!credentials.isEnabled()) {
            throw new BusinessRuleException("Cannot reactivate veterinarian: credentials are disabled.");
        }
        Role role = roleRepository.findByRole(Roles.ROLE_VETERINARIAN)
                .orElseThrow(() -> new BusinessRuleException("Required role ROLE_VETERINARIAN is missing."));

        if (credentials.getRoles() == null) {
            throw new BusinessRuleException("Veterinarian credentials have no roles collection.");
        }
        Set<Role> roles = new HashSet<>(credentials.getRoles());
        if (roles.stream().noneMatch(current -> current.getRole() == Roles.ROLE_VETERINARIAN)) {
            roles.add(role);
            credentials.setRoles(roles);
            credentialsRepository.save(credentials);
        }
        entity.setActive(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void validateActive(UUID externalId) {
        Veterinarian vet = entityByExternalId(externalId);

        if (!Boolean.TRUE.equals(vet.getActive())) {
            throw new BusinessRuleException("Cannot assign an inactive veterinarian.");
        }

        if (vet.getUser() == null || vet.getUser().getUserState() != UserState.ACTIVE) {
            throw new BusinessRuleException("Cannot assign a veterinarian with an inactive user account.");
        }
    }
}
