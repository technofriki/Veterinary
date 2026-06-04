package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalType;

import java.util.List;

public interface AnimalTypeService {

    AnimalType entityById(Long id);

    AnimalTypeResponse create(AnimalTypeRequest request);

    List<AnimalTypeResponse> findAll();

    AnimalTypeResponse findById(Long id);

    AnimalTypeResponse update(Long id, AnimalTypeRequest request);

    void delete(Long id);
}
