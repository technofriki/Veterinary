package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.model.Owner;

import java.util.List;
import java.util.UUID;

public interface OwnerService {

    OwnerResponse create(OwnerRequest request);

    Owner entityByExternalId(UUID externalId);

    List<OwnerResponse> findAll();

    OwnerResponse findById(UUID externalId);

    OwnerResponse findByDni(String dni);

    OwnerResponse update(UUID externalId, OwnerRequest request);

    void delete(UUID externalId);
}
