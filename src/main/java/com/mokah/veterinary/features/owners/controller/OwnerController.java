package com.mokah.veterinary.features.owners.controller;

import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> findAll(){
        return ResponseEntity.ok(ownerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(ownerService.findById(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<OwnerResponse> findByDni(@PathVariable String dni){
        return ResponseEntity.ok(ownerService.findByDni(dni));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> update(@PathVariable Long id, @Valid @RequestBody OwnerRequest request){
        return ResponseEntity.ok(ownerService.update(id,request));
    }

    @PostMapping
    public ResponseEntity<OwnerResponse> create(@Valid @RequestBody OwnerRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        ownerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
