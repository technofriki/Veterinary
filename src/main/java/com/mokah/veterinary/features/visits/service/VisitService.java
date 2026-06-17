package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.dto.WalkInVisitRequest;
import com.mokah.veterinary.features.visits.model.Visit;

import java.util.List;
import java.util.UUID;

public interface VisitService {

    VisitResponse create(VisitRequest dto);
    VisitResponse walkInCreate(WalkInVisitRequest dto);

    List<VisitResponse> findAll(
            UUID visitExternalId,
            String veterinarianName,
            String petName,
            Boolean walkIn
    );

    Visit entityByExternalId(UUID externalId);

    VisitResponse findById(UUID externalId);

    VisitResponse update(
            UUID externalId,
            VisitRequest dto
    );

    List<VisitResponse> findMedicalHistory(UUID petExternalId);
}
