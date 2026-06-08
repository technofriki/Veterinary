package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.model.Pet;

import java.util.List;
import java.util.UUID;

public interface PetService {

    PetResponse create(PetRequest dto);

    List<PetResponse> findAll();

    Pet entityByExternalId(UUID externalId);

    PetResponse findByExternalId(UUID externalId);

    PetResponse findByName(String name);

    PetResponse update(UUID externalId, PetRequest dto);

    void delete(UUID externalId);
}
