package com.mokah.veterinary.features.pets.controller;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@Valid @RequestBody PetRequest dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<PetResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public PetResponse findByExternalId(@PathVariable UUID externalId) {
        return service.findByExternalId(externalId);
    }

    @GetMapping("/name/{name}")
    public PetResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PutMapping("/{externalId}")
    public PetResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody PetRequest request
    ) {
        return service.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}