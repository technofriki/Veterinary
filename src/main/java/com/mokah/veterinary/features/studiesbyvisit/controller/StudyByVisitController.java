package com.mokah.veterinary.features.studiesbyvisit.controller;

import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitDTO;
import com.mokah.veterinary.features.studiesbyvisit.dto.StudyByVisitResponse;
import com.mokah.veterinary.features.studiesbyvisit.service.StudyByVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies-by-visits")
@RequiredArgsConstructor
public class StudyByVisitController {

    private final StudyByVisitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyByVisitResponse create(@Valid @RequestBody StudyByVisitDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public StudyByVisitResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<StudyByVisitResponse> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
