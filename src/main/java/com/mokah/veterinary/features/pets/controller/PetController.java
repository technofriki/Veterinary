package com.mokah.veterinary.features.pets.controller;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> findAll(){
        List<PetResponse> responseList = petService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity <PetResponse> getById(@PathVariable Long id){
        PetResponse response = petService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity <PetResponse> getByName(@PathVariable String name){
        PetResponse response = petService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity <PetResponse>  createPet(@RequestBody PetRequest request){
        PetResponse response = petService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long id, @RequestBody PetRequest request){
        PetResponse response = petService.update(id,request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PetResponse> deletePet(@PathVariable Long id){
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
