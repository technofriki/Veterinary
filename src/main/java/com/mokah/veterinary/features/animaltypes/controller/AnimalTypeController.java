package com.mokah.veterinary.features.animaltypes.controller;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animal-types")
@RequiredArgsConstructor
public class AnimalTypeController {

    private final AnimalTypeService service;

    @GetMapping
    public List<AnimalTypeResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AnimalTypeResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalTypeResponse create(@RequestBody AnimalTypeRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}