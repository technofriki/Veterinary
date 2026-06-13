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
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeterinarianServiceImpl implements VeterinarianService {

    private final VeterinarianRepository repository;
    private final VeterinarianMapper mapper;
    private final BranchService branchService;
    private final UserRepository userRepository;

    @Override
    public VeterinarianResponse create(VeterinarianCreateDTO dto) {

        if (repository.existsByUser_Email(dto.email())) {
            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + dto.email() + " already exists");
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
                && repository.existsByUser_Email(dto.email())) {

            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + dto.email() + " already exists");
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
    public void delete(UUID externalId) {
        Veterinarian entity = entityByExternalId(externalId);
        if(!entity.getActive()){
            throw new BusinessRuleException("The selected veterinarian is no longer active in the system.");
        }
        entity.setActive(false);
        repository.save(entity);
    }
}