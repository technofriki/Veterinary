package com.mokah.veterinary.features.appointments.controller;

import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(
            @Valid @RequestBody AppointmentCreateDTO dto
    ) {
        return service.create(dto);
    }

    @GetMapping
    public List<AppointmentResponse> findAll(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) UUID petExternalId,
            @RequestParam(required = false) UUID veterinarianExternalId
    ) {
        return service.findAll(
                date,
                reason,
                status,
                petExternalId,
                veterinarianExternalId
        );
    }

    @GetMapping("/{externalId}")
    public AppointmentResponse findById(
            @PathVariable UUID externalId
    ) {
        return service.findById(externalId);
    }

    @PutMapping("/{externalId}")
    public AppointmentResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody AppointmentUpdateDTO dto
    ) {
        return service.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID externalId
    ) {
        service.delete(externalId);
    }
}