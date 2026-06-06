package com.mokah.veterinary.features.visits.controller;

import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponse create(@Valid @RequestBody VisitRequest request) {
        return visitService.create(request);
    }

    @GetMapping
    public List<VisitResponse> findAll(
            @RequestParam(required = false) Long visitId,
            @RequestParam(required = false) String veterinarianName,
            @RequestParam(required = false) String petName
    ) {
        return visitService.findAll(visitId, veterinarianName, petName);
    }

    @GetMapping("/{id}")
    public VisitResponse findById(@PathVariable Long id){
        return visitService.findById(id);
    }

    @PutMapping("/{id}")
    public VisitResponse update(@PathVariable Long id, @Valid @RequestBody VisitRequest request){
        return visitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        visitService.delete(id);
    }

}
