package com.mokah.veterinary.features.ownersbypets.controller;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.service.OwnerByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owner-by-pet")
@RequiredArgsConstructor
public class OwnerByPetController {

    private final OwnerByPetService service;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_OWNERS')")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerByPetResponse create(@Valid @RequestBody OwnerByPetDTO request) {
        return service.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_OWNERS')")
    public List<OwnerByPetResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_OWNERS')")
    public OwnerByPetResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_OWNERS')")
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
