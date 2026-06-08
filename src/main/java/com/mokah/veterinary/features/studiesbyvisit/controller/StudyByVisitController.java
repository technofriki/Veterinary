package com.mokah.veterinary.features.studiesbyvisit.controller;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.service.StudyByVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public StudyByVisitResponse create(@Valid @RequestBody StudyByVisitDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    public StudyByVisitResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping
    public List<StudyByVisitResponse> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID externalId) {
        service.deactivate(externalId);
    }
}