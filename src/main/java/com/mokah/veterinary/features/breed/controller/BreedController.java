package com.mokah.veterinary.features.breed.controller;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.service.BreedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
public class BreedController {

    private final BreedService service;

    @GetMapping
    public List<BreedResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{externalId}")
    public BreedResponse findById(@PathVariable UUID externalId) {
        return service.findById(externalId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BreedResponse create(@Valid @RequestBody BreedRequest dto) {
        return service.create(dto);
    }

    @PutMapping("/{externalId}")
    public BreedResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody BreedRequest dto
    ) {
        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        service.delete(externalId);
    }
}