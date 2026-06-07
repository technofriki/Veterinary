package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;

import java.util.List;
import java.util.UUID;

public interface VeterinarianService {
    VeterinarianResponse create(VeterinarianCreateDTO dto);

    Veterinarian entityByExternalId(UUID externalId);

    VeterinarianResponse findById(UUID externalId);

    List<VeterinarianResponse> findAll(
            String firstName, String lastName, String licenseNumber, UUID branchExternalId);

    VeterinarianResponse update(UUID externalId, VeterinarianUpdateDTO dto);

    void delete(UUID externalId);
}
