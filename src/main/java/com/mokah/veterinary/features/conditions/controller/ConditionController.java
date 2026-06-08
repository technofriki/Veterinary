package com.mokah.veterinary.features.conditions.controller;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.service.ConditionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConditionResponse create(
            @Valid @RequestBody ConditionRequest dto
    ) {
        return service.create(dto);
    }

    @GetMapping
    public List<ConditionResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public ConditionResponse findById(
            @PathVariable UUID externalId
    ) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public ConditionResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody ConditionRequest dto
    ) {
        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID externalId
    ) {
        service.delete(externalId);
    }
}