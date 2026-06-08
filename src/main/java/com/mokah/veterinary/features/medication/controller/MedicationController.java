package com.mokah.veterinary.features.medication.controller;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService service;

    @GetMapping
    public List<MedicationResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public MedicationResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping("/name/{name}")
    public MedicationResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicationResponse create(@Valid @RequestBody MedicationRequest dto) {
        return service.create(dto);
    }

    @PutMapping("/{externalId}")
    public MedicationResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody MedicationRequest dto) {

        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {

        service.delete(externalId);
    }
}