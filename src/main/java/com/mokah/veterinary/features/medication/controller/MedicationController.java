package com.mokah.veterinary.features.medication.controller;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_MEDICATIONS')")
    public List<MedicationResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_MEDICATIONS')")
    public MedicationResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('VIEW_MEDICATIONS')")
    public MedicationResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MANAGE_MEDICATIONS')")
    public MedicationResponse create(@Valid @RequestBody MedicationRequest dto) {
        return service.create(dto);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('MANAGE_MEDICATIONS')")
    public MedicationResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody MedicationRequest dto) {

        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MANAGE_MEDICATIONS')")
    public void delete(@PathVariable UUID externalId) {

        service.delete(externalId);
    }
}