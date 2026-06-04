package com.mokah.veterinary.features.breed.service;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;

import java.util.List;

public interface BreedService {

    BreedEntity entityById(Long id);

    BreedResponse findById(Long id);

    List<BreedResponse> findAll();

    BreedResponse create(BreedRequest request);

    BreedResponse update(Long id, BreedRequest request);

    void delete(Long id);
}
