package com.mokah.veterinary.features.veterinarians.controller;

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarians")
@RequiredArgsConstructor
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianResponse create(@Valid @RequestBody VeterinarianCreateDTO request) {
        return veterinarianService.create(request);
    }

    @GetMapping
    public List<VeterinarianResponse> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) Long branchId
    ) {
        return veterinarianService.findAll(firstName, lastName, licenseNumber, branchId);
    }

    @GetMapping("/{id}")
    public VeterinarianResponse findById(@PathVariable Long id) {
        return veterinarianService.findById(id);
    }

    @PutMapping("/{id}")
    public VeterinarianResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VeterinarianUpdateDTO request) {

        return veterinarianService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        veterinarianService.delete(id);
    }
}
