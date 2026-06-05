package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.entity.Owner;

import java.util.List;

public interface OwnerService {
    OwnerResponse create(OwnerRequest request);

    Owner entityById(Long id);
    List<OwnerResponse> findAll();
    OwnerResponse findById(Long id);
    OwnerResponse findByDni(String dni);

    OwnerResponse update(Long id, OwnerRequest request);

    void delete(Long id);
}
