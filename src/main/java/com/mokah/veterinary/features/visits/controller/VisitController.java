package com.mokah.veterinary.features.visits.controller;

import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public VisitResponse create(@Valid @RequestBody VisitRequest dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<VisitResponse> findAll(
            @RequestParam(required = false) UUID visitExternalId,
            @RequestParam(required = false) String veterinarianName,
            @RequestParam(required = false) String petName) {

        return service.findAll(visitExternalId, veterinarianName, petName);
    }

    @GetMapping("/{externalId}")
    public VisitResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public VisitResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody VisitRequest dto) {

        return service.update(externalId, dto);
    }

    @GetMapping("/medical-history/{petExternalId}")
    public List<VisitResponse> medicalHistory(
            @PathVariable UUID petExternalId) {

        return service.findMedicalHistory(petExternalId);
    }

}
