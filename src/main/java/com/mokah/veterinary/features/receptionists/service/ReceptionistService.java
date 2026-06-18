package com.mokah.veterinary.features.receptionists.service;

import com.mokah.veterinary.features.receptionists.dto.ReceptionistCreateDTO;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistUpdateDTO;
import com.mokah.veterinary.features.receptionists.model.Receptionist;

import java.util.List;
import java.util.UUID;

public interface ReceptionistService {
    ReceptionistResponse create(ReceptionistCreateDTO dto);

    Receptionist entityByExternalId(UUID externalId);

    ReceptionistResponse findById(UUID externalId);

    List<ReceptionistResponse> findAll(
            String firstName, String lastName, UUID branchExternalId);

    ReceptionistResponse update(UUID externalId, ReceptionistUpdateDTO dto);

    void delete(UUID externalId);
}
