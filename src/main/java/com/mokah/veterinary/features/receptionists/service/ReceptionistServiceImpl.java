package com.mokah.veterinary.features.receptionists.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistCreateDTO;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistUpdateDTO;
import com.mokah.veterinary.features.receptionists.exception.ReceptionistEmailExistsException;
import com.mokah.veterinary.features.receptionists.exception.ReceptionistPhoneExistsException;
import com.mokah.veterinary.features.receptionists.mapper.ReceptionistMapper;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.receptionists.specification.ReceptionistSpecification;
import com.mokah.veterinary.security.enums.Roles;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.model.Role;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.repository.RoleRepository;
import com.mokah.veterinary.security.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceptionistServiceImpl implements ReceptionistService {

    private final ReceptionistRepository repository;
    private final ReceptionistMapper mapper;
    private final BranchService branchService;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ReceptionistResponse create(ReceptionistCreateDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new ReceptionistEmailExistsException(
                    "User with email " + dto.email() + " already exists");
        }

        if (credentialsRepository.findByUsername(dto.email()).isPresent()) {
            throw new ReceptionistEmailExistsException(
                    "Username " + dto.email() + " already exists");
        }

        if (repository.existsByPhone(dto.phone())) {
            throw new ReceptionistPhoneExistsException(
                    "Receptionist with phone " + dto.phone() + " already exists");
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .userState(UserState.ACTIVE)
                .build();
        user = userRepository.save(user);

        Role receptionistRole = roleRepository.findByRole(Roles.ROLE_RECEPTIONIST)
                .orElseThrow(() -> new IllegalStateException("Receptionist role not found"));

        String encodedPassword = passwordEncoder.encode(dto.password());

        Credentials credentials = Credentials.builder()
                .username(dto.email())
                .password(encodedPassword)
                .enabled(true)
                .user(user)
                .roles(Set.of(receptionistRole))
                .build();

        String refreshToken = jwtService.generateRefreshToken(credentials);
        credentials.setRefreshToken(refreshToken);

        credentialsRepository.save(credentials);

        Receptionist entity = mapper.toEntity(dto);
        entity.setUser(user);

        Branch branch = branchService.entityByExternalId(dto.branchExternalId());
        entity.setBranch(branch);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Receptionist entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist", "externalId", externalId));
    }

    @Override
    public ReceptionistResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<ReceptionistResponse> findAll(
            String firstName,
            String lastName,
            UUID branchExternalId) {

        PredicateSpecification<Receptionist> spec = PredicateSpecification.allOf(
                ReceptionistSpecification.hasFirstName(firstName),
                ReceptionistSpecification.hasLastName(lastName),
                ReceptionistSpecification.hasBranchExternalId(branchExternalId)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    @Transactional
    public ReceptionistResponse update(UUID externalId, ReceptionistUpdateDTO dto) {

        Receptionist entity = entityByExternalId(externalId);

        if (dto.email() != null
                && !dto.email().equalsIgnoreCase(entity.getUser().getEmail())
                && userRepository.existsByEmail(dto.email())) {

            throw new ReceptionistEmailExistsException(
                    "User with email " + dto.email() + " already exists");
        }

        if (dto.phone() != null
                && !dto.phone().equalsIgnoreCase(entity.getPhone())
                && repository.existsByPhone(dto.phone())) {

            throw new ReceptionistPhoneExistsException(
                    "Receptionist with phone " + dto.phone() + " already exists");
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
        Receptionist entity = entityByExternalId(externalId);
        if(!entity.getActive()){
            throw new BusinessRuleException("The selected receptionist is no longer active in the system.");
        }
        entity.setActive(false);
        repository.save(entity);
    }
}
