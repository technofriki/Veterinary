package com.mokah.veterinary.features.animaltypes.controller;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animal-types")
@RequiredArgsConstructor
public class AnimalTypeController {

    private final AnimalTypeService service;

    @GetMapping
    public List<AnimalTypeResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public AnimalTypeResponse getById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalTypeResponse create(@Valid @RequestBody AnimalTypeRequest request) {
        return service.create(request);
    }

    @PutMapping("/{externalId}")
    public AnimalTypeResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody AnimalTypeRequest request
    ) {
        return service.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}