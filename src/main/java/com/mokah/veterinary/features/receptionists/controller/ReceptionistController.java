package com.mokah.veterinary.features.receptionists.controller;

import com.mokah.veterinary.features.receptionists.dto.ReceptionistCreateDTO;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistResponse;
import com.mokah.veterinary.features.receptionists.dto.ReceptionistUpdateDTO;
import com.mokah.veterinary.features.receptionists.service.ReceptionistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ReceptionistResponse create(@Valid @RequestBody ReceptionistCreateDTO dto) {
        return receptionistService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENT', 'VETERINARIAN')")
    public List<ReceptionistResponse> findAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) UUID branchExternalId) {

        return receptionistService.findAll(firstName, lastName, branchExternalId);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENT', 'VETERINARIAN')")
    public ReceptionistResponse findById(@PathVariable UUID externalId) {
        return receptionistService.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ReceptionistResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody ReceptionistUpdateDTO dto) {

        return receptionistService.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID externalId) {
        receptionistService.delete(externalId);
    }
}
