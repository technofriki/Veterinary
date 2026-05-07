package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;

import java.util.List;

public interface BreedServiceInterface {

    BreedEntity create (BreedRequest request);

    List<BreedResponse> findAll();

    BreedResponse findById(Long id);

    void delete (Long id);
}
