package com.mokah.veterinary.features.breed.controller;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.service.BreedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
public class BreedController {

    private final BreedService breedService;

    @GetMapping
    public List<BreedResponse> findAll() {
        return breedService.findAll();
    }

    @GetMapping("/{id}")
    public BreedResponse findById(@PathVariable Long id) {
        return breedService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BreedResponse create(@Valid @RequestBody BreedRequest request) {
        return breedService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        breedService.delete(id);
    }
}
