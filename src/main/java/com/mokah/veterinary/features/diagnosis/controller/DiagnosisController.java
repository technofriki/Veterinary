package com.mokah.veterinary.features.diagnosis.controller;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequest;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisResponse>> findAll() {
        return ResponseEntity.ok(diagnosisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(diagnosisService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DiagnosisResponse> create(@Valid @RequestBody DiagnosisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        diagnosisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
