package com.mokah.veterinary.features.prescriptions.controller;

import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_PRESCRIPTIONS')")
    public PrescriptionResponse create(@Valid @RequestBody PrescriptionRequest dto) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_PRESCRIPTIONS')")
    public PrescriptionResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PRESCRIPTIONS')")
    public List<PrescriptionResponse> findAll(
            @RequestParam(required = false) UUID diagnosisExternalId,
            @RequestParam(required = false) UUID petExternalId) {

        return service.findAll(diagnosisExternalId, petExternalId);
    }
}
