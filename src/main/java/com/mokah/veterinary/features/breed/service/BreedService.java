package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.model.Breed;

import java.util.List;
import java.util.UUID;

public interface BreedService {

    Breed entityByExternalId(UUID externalId);

    BreedResponse findById(UUID externalId);

    List<BreedResponse> findAll();

    BreedResponse create(BreedRequest dto);

    BreedResponse update(UUID externalId, BreedRequest dto);

    void delete(UUID externalId);
}
