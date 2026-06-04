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

@Service
@RequiredArgsConstructor
public class VeterinarianServiceImpl implements VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final VeterinarianMapper veterinarianMapper;
    private final BranchService branchService;

    @Override
    public VeterinarianResponse create(VeterinarianCreateDTO request) {
        if (veterinarianRepository.existsByEmail(request.email())) {
            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + request.email() + " already exists");

        }

        if (veterinarianRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license number " + request.licenseNumber() + " Already exists.");
        }

        if (veterinarianRepository.existsByPhone(request.phone())) {
            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + request.phone() + " already exists");
        }

        Veterinarian entity = veterinarianMapper.toEntity(request);
        Branch branch = branchService.entityById(request.branchId());
        entity.setBranch(branch);

        return veterinarianMapper.toResponse(veterinarianRepository.save(entity));
    }

    @Override
    public Veterinarian entityById(Long id) {
        return veterinarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian", id));
    }

    @Override
    public VeterinarianResponse findById(Long id) {
        return veterinarianMapper.toResponse(entityById(id));
    }

    @Override
    public List<VeterinarianResponse> findAll(String firstName, String lastName, String licenseNumber, Long branchId) {
        PredicateSpecification<Veterinarian> spec = PredicateSpecification.allOf(
                VeterinarianSpecification.hasFirstName(firstName),
                VeterinarianSpecification.hasLastName(lastName),
                VeterinarianSpecification.hasLicenseNumber(licenseNumber),
                VeterinarianSpecification.hasBranchId(branchId)
        );

        return veterinarianMapper.toResponseList(veterinarianRepository.findAll(spec));
    }

    @Override
    public VeterinarianResponse update(Long id, VeterinarianUpdateDTO request) {
        Veterinarian entity = entityById(id);

        if (!request.email().equalsIgnoreCase(entity.getEmail())
                && veterinarianRepository.existsByEmail(request.email())) {
            throw new VeterinarianEmailExistsException(
                    "Veterinarian with email " + request.email() + " already exists");
        }

        if (!request.phone().equalsIgnoreCase(entity.getPhone())
                && veterinarianRepository.existsByPhone(request.phone())) {
            throw new VeterinarianPhoneExistsException(
                    "Veterinarian with phone " + request.phone() + " already exists");
        }

        if (!request.licenseNumber().equalsIgnoreCase(entity.getLicenseNumber())
                && veterinarianRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new VeterinarianLicenseExistsException(
                    "Veterinarian with license " + request.licenseNumber() + " number already exists");
        }

        veterinarianMapper.update(entity, request);

        if (request.branchId() != null) {
            Branch branch = branchService.entityById(request.branchId());
            entity.setBranch(branch);
        }

        return veterinarianMapper.toResponse(veterinarianRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Veterinarian entity = entityById(id);
        veterinarianRepository.delete(entity);
    }
}
