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
import java.util.UUID;

@RestController
@RequestMapping("/api/veterinarians")
@RequiredArgsConstructor
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianResponse create(@Valid @RequestBody VeterinarianCreateDTO dto) {
        return veterinarianService.create(dto);
    }

    @GetMapping
    public List<VeterinarianResponse> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) UUID branchExternalId
    ) {
        return veterinarianService.findAll(firstName, lastName, licenseNumber, branchExternalId);
    }

    @GetMapping("/{externalId}")
    public VeterinarianResponse findById(@PathVariable UUID externalId) {
        return veterinarianService.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public VeterinarianResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody VeterinarianUpdateDTO dto
    ) {
        return veterinarianService.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID externalId) {
        veterinarianService.delete(externalId);
    }
}
