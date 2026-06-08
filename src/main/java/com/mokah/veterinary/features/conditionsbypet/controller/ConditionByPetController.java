package com.mokah.veterinary.features.conditionsbypet.controller;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.service.ConditionByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ConditionByPetResponse create(
            @Valid @RequestBody ConditionByPetDTO dto
    ) {
        return service.create(dto);
    }

    @GetMapping("/{externalId}")
    public ConditionByPetResponse findById(
            @PathVariable UUID externalId
    ) {
        return service.findById(externalId);
    }

    @GetMapping
    public List<ConditionByPetResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{externalId}")
    public ConditionByPetResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody ConditionByPetUpdateDTO dto
    ) {
        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable UUID externalId
    ) {
        service.deactivate(externalId);
    }
}
