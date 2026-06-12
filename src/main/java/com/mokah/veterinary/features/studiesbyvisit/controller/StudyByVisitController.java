package com.mokah.veterinary.features.studiesbyvisit.controller;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.service.StudyByVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/studies-by-visits")
@RequiredArgsConstructor
public class StudyByVisitController {

    private final StudyByVisitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public StudyByVisitResponse create(@Valid @RequestBody StudyByVisitDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public StudyByVisitResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<StudyByVisitResponse> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable UUID externalId) {
        service.deactivate(externalId);
    }
}