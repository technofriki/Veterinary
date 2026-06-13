package com.mokah.veterinary.features.studies.controller;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public StudyResponse create(@Valid @RequestBody StudyRequest dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<StudyResponse> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {

        return service.findAll(name, description);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public StudyResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_RECORDS')")
    public StudyResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody StudyRequest dto) {

        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}