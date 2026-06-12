package com.mokah.veterinary.features.diagnosis.controller;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_DIAGNOSES')")
    public DiagnosisResponse create(@Valid @RequestBody DiagnosisRequest dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_DIAGNOSES')")
    public List<DiagnosisResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_DIAGNOSES')")
    public DiagnosisResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_DIAGNOSES')")
    public DiagnosisResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody DiagnosisRequest dto) {

        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('DELETE_DIAGNOSES')")
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}