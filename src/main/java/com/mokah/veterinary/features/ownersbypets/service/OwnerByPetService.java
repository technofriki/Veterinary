package com.mokah.veterinary.features.ownersbypets.service;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;

import java.util.List;
import java.util.UUID;

public interface OwnerByPetService {

    OwnerByPetResponse create(OwnerByPetDTO request);

    OwnerByPet entityByExternalId(UUID externalId);

    OwnerByPetResponse findById(UUID externalId);

    List<OwnerByPetResponse> findAll();

    OwnerByPetResponse update(UUID externalId, OwnerByPetDTO request);

    void delete(UUID externalId);
}
