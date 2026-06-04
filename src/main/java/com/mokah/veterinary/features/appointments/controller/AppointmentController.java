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

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentCreateDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<AppointmentResponse> findAll(
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId
    ) {
        return service.findAll(date, reason, status, petId, veterinarianId);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateDTO dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}