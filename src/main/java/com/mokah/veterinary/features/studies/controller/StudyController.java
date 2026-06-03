package com.mokah.veterinary.features.studies.controller;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.service.StudyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyService service;

    public StudyController(StudyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StudyResponse> create(@Valid @RequestBody StudyRequest request){

    }

    @GetMapping
    public ResponseEntity<List<StudyResponse>> findAll(){

    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyResponse> findById(@PathVariable Long id){

    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyResponse> update(@PathVariable Long id, @Valid @RequestBody StudyRequest request){

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

    }
}
