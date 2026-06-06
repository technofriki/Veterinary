package com.mokah.veterinary.features.conditionsbypet.controller;

import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.service.ConditionByPetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions-by-pets")
@RequiredArgsConstructor
public class ConditionByPetController {

    private final ConditionByPetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConditionByPetResponse create(@Valid @RequestBody ConditionByPetDTO dto){
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ConditionByPetResponse findById(@PathVariable Long id){
        return service.findById(id);
    }

    @GetMapping
    public List<ConditionByPetResponse> findAll(){
        return service.findAll();
    }

    @PutMapping("/{id}")
    public ConditionByPetResponse update (@PathVariable Long id, @Valid @RequestBody ConditionByPetUpdateDTO dto){
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id){
        service.deactivate(id);
    }
}
