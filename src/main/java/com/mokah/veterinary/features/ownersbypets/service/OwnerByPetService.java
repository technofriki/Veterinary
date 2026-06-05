package com.mokah.veterinary.features.ownersbypets.service;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;

import java.util.List;

public interface OwnerByPetService {
    OwnerByPetResponse create(OwnerByPetDTO request);

    OwnerByPet entityById(Long id);
    OwnerByPetResponse findById(Long id);
    List<OwnerByPetResponse> findAll();

    OwnerByPetResponse update(Long id, OwnerByPetDTO request);

    void delete(Long id);
}
