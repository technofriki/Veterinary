package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.dto.VisitUpdateDTO;
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
            VisitUpdateDTO dto
    );

    List<VisitResponse> findMedicalHistory(UUID petExternalId);

    List<DiagnosisResponse> findDiagnosesByVisit(UUID visitExternalId);

    List<StudyResponse> findStudiesByVisit(UUID visitExternalId);

    List<PrescriptionResponse> findPrescriptionsByVisit(UUID visitExternalId);
}