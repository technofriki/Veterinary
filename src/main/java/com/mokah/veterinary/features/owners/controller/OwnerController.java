package com.mokah.veterinary.features.owners.controller;

import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_OWNERS')")
    public List<OwnerResponse> findAll() {
        return ownerService.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_OWNERS')")
    public OwnerResponse findById(@PathVariable UUID externalId) {
        return ownerService.findById(externalId);
    }

    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasAuthority('VIEW_OWNERS')")
    public OwnerResponse findByDni(@PathVariable String dni) {
        return ownerService.findByDni(dni);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_OWNERS')")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponse create(@Valid @RequestBody OwnerRequest request) {
        return ownerService.create(request);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('UPDATE_OWNERS')")
    public OwnerResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody OwnerRequest request) {

        return ownerService.update(externalId, request);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        ownerService.delete(externalId);
    }
}
