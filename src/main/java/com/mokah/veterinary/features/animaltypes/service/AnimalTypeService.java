package com.mokah.veterinary.features.animaltypes.service;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.entity.AnimalType;

import java.util.List;
import java.util.UUID;

public interface AnimalTypeService {

    AnimalType entityByExternalId(UUID externalId);

    AnimalTypeResponse create(AnimalTypeRequest request);

    List<AnimalTypeResponse> findAll();

    AnimalTypeResponse findById(UUID externalId);

    AnimalTypeResponse update(UUID externalId, AnimalTypeRequest request);

    void delete(UUID externalId);
}
