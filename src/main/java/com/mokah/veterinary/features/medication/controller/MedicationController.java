package com.mokah.veterinary.features.medication.controller;

import com.mokah.veterinary.features.medication.dto.MedicationRequest;
import com.mokah.veterinary.features.medication.dto.MedicationResponse;
import com.mokah.veterinary.features.medication.service.MedicationServiceInterface;
import com.mokah.veterinary.features.users.dto.UserRequest;
import com.mokah.veterinary.features.users.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationServiceInterface medicationService;

    public MedicationController(MedicationServiceInterface medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ResponseEntity<List<MedicationResponse>> findAllMedications() {
        List<MedicationResponse> responseList = medicationService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationResponse> getMedicationById(@PathVariable Long id) {
        MedicationResponse response = medicationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicationName/{medicationName}")
    public ResponseEntity<MedicationResponse> getMedicationByName(@PathVariable String medicationName) {
        MedicationResponse response = medicationService.findByName(medicationName);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MedicationResponse> createMedication(@RequestBody MedicationRequest medicationRequest) {
        MedicationResponse response = medicationService.create(medicationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationResponse> updateMedication(@PathVariable Long id, @RequestBody MedicationRequest medicationRequest) {
        MedicationResponse response = medicationService.update(id,medicationRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MedicationResponse> deleteMedication(@PathVariable Long id) {
        medicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
