package com.mokah.veterinary.features.diagnosis.controller;

import com.mokah.veterinary.features.diagnosis.dto.DiagnosisRequestDTO;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponseDTO;
import com.mokah.veterinary.features.diagnosis.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public ResponseEntity<DiagnosisResponseDTO> create(@Valid @RequestBody DiagnosisRequestDTO request) {
        return ResponseEntity.ok(diagnosisService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisResponseDTO>> findAll() {
        return ResponseEntity.ok(diagnosisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(diagnosisService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        diagnosisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
