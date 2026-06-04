package com.mokah.veterinary.features.pets.controller;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@Valid @RequestBody PetRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<PetResponse> findAll(
    ) {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PetResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/name/{name}")
    public PetResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @PutMapping("/{id}")
    public PetResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}