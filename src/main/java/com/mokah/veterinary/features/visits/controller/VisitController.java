package com.mokah.veterinary.features.visits.controller;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.dto.VisitUpdateDTO;
import com.mokah.veterinary.features.visits.dto.WalkInVisitRequest;
import com.mokah.veterinary.features.visits.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public VisitResponse create(@Valid @RequestBody VisitRequest dto) {
        return service.create(dto);
    }

    @PostMapping("/walk-in")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public VisitResponse walkInCreate(@Valid @RequestBody WalkInVisitRequest dto) {
        return service.walkInCreate(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<VisitResponse> findAll(
            @RequestParam(required = false) UUID visitExternalId,
            @RequestParam(required = false) String veterinarianName,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Boolean walkIn
    ) {
        return service.findAll(visitExternalId, veterinarianName, petName, walkIn);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public VisitResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_RECORDS')")
    public VisitResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody VisitUpdateDTO dto) {

        return service.update(externalId, dto);
    }

    @GetMapping("/medical-history/{petExternalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<VisitResponse> medicalHistory(@PathVariable UUID petExternalId) {
        return service.findMedicalHistory(petExternalId);
    }

    @GetMapping("/{externalId}/diagnoses")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<DiagnosisResponse> findDiagnosesByVisit(
            @PathVariable UUID externalId) {

        return service.findDiagnosesByVisit(externalId);
    }

    @GetMapping("/{externalId}/studies")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<StudyResponse> findStudiesByVisit(
            @PathVariable UUID externalId) {

        return service.findStudiesByVisit(externalId);
    }

    @GetMapping("/{externalId}/prescriptions")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<PrescriptionResponse> findPrescriptionsByVisit(
            @PathVariable UUID externalId) {

        return service.findPrescriptionsByVisit(externalId);
    }
}
