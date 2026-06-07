package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.entity.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
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

    private final VeterinarianRepository veterinarianRepository;
    private final VeterinarianMapper veterinarianMapper;
    private final BranchService branchService;

    @Override
    public VeterinarianResponse create(VeterinarianCreateDTO dto) {

        if (veterinarianRepository.existsByEmail(dto.email())) {
            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + dto.email() + " already exists");
        }

        if (veterinarianRepository.existsByLicenseNumber(dto.licenseNumber())) {
            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license number " + dto.licenseNumber() + " already exists.");
        }

        if (veterinarianRepository.existsByPhone(dto.phone())) {
            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + dto.phone() + " already exists");
        }

        Veterinarian entity = veterinarianMapper.toEntity(dto);

        Branch branch = branchService.entityById(dto.branchId());
        entity.setBranch(branch);

        return veterinarianMapper.toResponse(veterinarianRepository.save(entity));
    }

    @Override
    public Veterinarian entityByExternalId(UUID externalId) {
        return veterinarianRepository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Veterinarian", "externalId", externalId));
    }

    @Override
    public VeterinarianResponse findById(UUID externalId) {
        return veterinarianMapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<VeterinarianResponse> findAll(
            String firstName,
            String lastName,
            String licenseNumber,
            Long branchId
    ) {

        PredicateSpecification<Veterinarian> spec = PredicateSpecification.allOf(
                VeterinarianSpecification.hasFirstName(firstName),
                VeterinarianSpecification.hasLastName(lastName),
                VeterinarianSpecification.hasLicenseNumber(licenseNumber),
                VeterinarianSpecification.hasBranchId(branchId)
        );

        return veterinarianMapper.toResponseList(
                veterinarianRepository.findAll(spec)
        );
    }

    @Override
    public VeterinarianResponse update(UUID externalId, VeterinarianUpdateDTO dto) {

        Veterinarian entity = entityByExternalId(externalId);

        if (dto.email() != null
                && !dto.email().equalsIgnoreCase(entity.getEmail())
                && veterinarianRepository.existsByEmail(dto.email())) {

            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + dto.email() + " already exists");
        }

        if (dto.phone() != null
                && !dto.phone().equalsIgnoreCase(entity.getPhone())
                && veterinarianRepository.existsByPhone(dto.phone())) {

            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + dto.phone() + " already exists");
        }

        if (dto.licenseNumber() != null
                && !dto.licenseNumber().equalsIgnoreCase(entity.getLicenseNumber())
                && veterinarianRepository.existsByLicenseNumber(dto.licenseNumber())) {

            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license " + dto.licenseNumber() + " number already exists");
        }

        veterinarianMapper.update(entity, dto);

        if (dto.branchId() != null) {
            Branch branch = branchService.entityById(dto.branchId());
            entity.setBranch(branch);
        }

        return veterinarianMapper.toResponse(
                veterinarianRepository.save(entity)
        );
    }

    @Override
    public void delete(UUID externalId) {
        veterinarianRepository.delete(entityByExternalId(externalId));
    }
}
