package com.mokah.veterinary.features.conditionsbypet.controller;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.service.ConditionByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conditions-by-pets")
@RequiredArgsConstructor
public class ConditionByPetController {

    private final ConditionByPetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_CLINICAL_RECORDS')")
    public ConditionByPetResponse create(@Valid @RequestBody ConditionByPetDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public ConditionByPetResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLINICAL_RECORDS')")
    public List<ConditionByPetResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_CLINICAL_RECORDS')")
    public ConditionByPetResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody ConditionByPetUpdateDTO dto) {

        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable UUID externalId) {
        service.deactivate(externalId);
    }
}
