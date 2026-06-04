package com.mokah.veterinary.features.veterinarians.service;

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;

import java.util.List;

public interface VeterinarianService {
    VeterinarianResponse create(VeterinarianCreateDTO request);

    Veterinarian entityById(Long id);

    VeterinarianResponse findById(Long id);

    List<VeterinarianResponse> findAll(
            String firstName, String lastName, String licenseNumber, Long branchId);

    VeterinarianResponse update(Long id, VeterinarianUpdateDTO request);

    void delete(Long id);
}
