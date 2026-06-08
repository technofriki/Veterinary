package com.mokah.veterinary.features.ownersbypets.controller;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.service.OwnerByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owner-by-pet")
@RequiredArgsConstructor
public class OwnerByPetController {

    private final OwnerByPetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerByPetResponse create(@Valid @RequestBody OwnerByPetDTO request) {
        return service.create(request);
    }

    @GetMapping
    public List<OwnerByPetResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public OwnerByPetResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public OwnerByPetResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody OwnerByPetDTO request) {
        return service.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}
