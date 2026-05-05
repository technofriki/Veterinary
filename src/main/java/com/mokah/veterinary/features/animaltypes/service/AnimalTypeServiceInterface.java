package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;

import java.util.List;

public interface AnimalTypeServiceInterface {

    AnimalTypeResponse create(AnimalTypeRequest request);

    List<AnimalTypeResponse> findAll();

    AnimalTypeResponse findById(Long id);

    void delete(Long id);
}
