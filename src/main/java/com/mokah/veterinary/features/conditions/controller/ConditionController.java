package com.mokah.veterinary.features.conditions.controller;

import com.mokah.veterinary.features.conditions.dto.ConditionRequest;
import com.mokah.veterinary.features.conditions.dto.ConditionResponse;
import com.mokah.veterinary.features.conditions.service.ConditionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/condition")
public class ConditionController {

    private final ConditionService conditionService;

    public ConditionController(ConditionService conditionService) {
        this.conditionService = conditionService;
    }

    @GetMapping
    public ResponseEntity<List<ConditionResponse>> findAll(){
        return ResponseEntity.ok(conditionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConditionResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(conditionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ConditionResponse> create(@Valid @RequestBody ConditionRequest request){
        return ResponseEntity.ok(conditionService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        conditionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
