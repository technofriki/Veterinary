package com.mokah.veterinary.features.ownersbypets.controller;

import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetDTO;
import com.mokah.veterinary.features.ownersbypets.dto.OwnerByPetResponse;
import com.mokah.veterinary.features.ownersbypets.service.OwnerByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners-by-pets")
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

    @GetMapping("/{id}")
    public OwnerByPetResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public OwnerByPetResponse update(@PathVariable Long id, @Valid @RequestBody OwnerByPetDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
