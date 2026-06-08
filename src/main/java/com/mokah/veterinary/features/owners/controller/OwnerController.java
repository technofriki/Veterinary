package com.mokah.veterinary.features.owners.controller;

import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public List<OwnerResponse> findAll() {
        return ownerService.findAll();
    }

    @GetMapping("/{externalId}")
    public OwnerResponse findById(@PathVariable UUID externalId) {
        return ownerService.findById(externalId);
    }

    @GetMapping("/dni/{dni}")
    public OwnerResponse findByDni(@PathVariable String dni) {
        return ownerService.findByDni(dni);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponse create(@Valid @RequestBody OwnerRequest request) {
        return ownerService.create(request);
    }

    @PutMapping("/{externalId}")
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
