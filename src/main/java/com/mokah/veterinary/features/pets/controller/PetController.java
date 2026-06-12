package com.mokah.veterinary.features.pets.controller;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('CREATE_PETS')")
    public PetResponse create(@Valid @RequestBody PetRequest request) {

        return service.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PETS')")
    public List<PetResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_PETS')")
    public PetResponse findByExternalId(@PathVariable UUID externalId) {
        return service.findByExternalId(externalId);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('VIEW_PETS')")
    public PetResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_PETS')")
    public PetResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody PetRequest request) {

        return service.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('DELETE_PETS')")
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}