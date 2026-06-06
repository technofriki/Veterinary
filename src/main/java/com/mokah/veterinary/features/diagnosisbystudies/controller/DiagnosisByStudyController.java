package com.mokah.veterinary.features.diagnosisbystudies.controller;

import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyDTO;
import com.mokah.veterinary.features.diagnosisbystudies.dto.DiagnosisByStudyResponse;
import com.mokah.veterinary.features.diagnosisbystudies.service.DiagnosisByStudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis-by-studies")
@RequiredArgsConstructor
public class DiagnosisByStudyController {

    private final DiagnosisByStudyService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisByStudyResponse create(@Valid @RequestBody DiagnosisByStudyDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public DiagnosisByStudyResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<DiagnosisByStudyResponse> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
