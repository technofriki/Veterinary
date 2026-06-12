package com.mokah.veterinary.features.veterinarians.controller; // Ajustá el paquete si es necesario

import com.mokah.veterinary.features.veterinarians.dto.VeterinarianCreateDTO;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianUpdateDTO;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTANTE
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
    @PreAuthorize("hasRole('ADMIN')") // Solo el administrador puede contratar/crear veterinarios
    public VeterinarianResponse create(@Valid @RequestBody VeterinarianCreateDTO dto) {
        return veterinarianService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENT', 'VETERINARIAN')") // Cualquiera puede ver la lista
    public List<VeterinarianResponse> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) UUID branchExternalId) {

        return veterinarianService.findAll(firstName, lastName, licenseNumber, branchExternalId);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENT', 'VETERINARIAN')") // Cualquiera puede ver el detalle
    public VeterinarianResponse findById(@PathVariable UUID externalId) {
        return veterinarianService.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasRole('ADMIN')") // Solo el administrador puede modificar datos del profesional
    public VeterinarianResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody VeterinarianUpdateDTO dto) {

        return veterinarianService.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')") // Acción destructiva reservada estrictamente al administrador
    public void delete(@PathVariable UUID externalId) {
        veterinarianService.delete(externalId);
    }
}