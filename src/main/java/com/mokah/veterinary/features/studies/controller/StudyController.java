package com.mokah.veterinary.features.studies.controller;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public StudyResponse create(
            @Valid @RequestBody StudyRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public List<StudyResponse> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        return service.findAll(name, description);
    }

    @GetMapping("/{externalId}")
    public StudyResponse findById(
            @PathVariable UUID externalId
    ) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public StudyResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody StudyRequest request
    ) {
        return service.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID externalId
    ) {
        service.delete(externalId);
    }
}