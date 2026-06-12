package com.mokah.veterinary.features.diagnosisbystudies.controller;

import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.service.DiagnosisByStudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diagnosis-by-studies")
@RequiredArgsConstructor
public class DiagnosisByStudyController {

    private final DiagnosisByStudyService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public DiagnosisByStudyResponse create(@Valid @RequestBody DiagnosisByStudyDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public DiagnosisByStudyResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<DiagnosisByStudyResponse> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable UUID externalId) {
        service.deactivate(externalId);
    }
}
