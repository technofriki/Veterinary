package com.mokah.veterinary.features.breed.controller;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import com.mokah.veterinary.features.breed.dto.BreedResponse;
import com.mokah.veterinary.features.breed.entity.BreedEntity;
import com.mokah.veterinary.features.breed.service.BreedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breed")
public class BreedController {

    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    @GetMapping
    public ResponseEntity<List<BreedResponse>> findAllBreed(){
        List<BreedResponse> responseList = breedService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreedResponse> getById(@PathVariable Long id){
        BreedResponse response = breedService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BreedResponse> getByName(@PathVariable String name){
        BreedResponse response = breedService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BreedResponse> createBreed (@RequestBody BreedRequest request){
        BreedResponse created = breedService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<BreedResponse> updateBreed(@PathVariable Long id, @RequestBody BreedRequest request){
        BreedResponse updated = breedService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BreedResponse> deleteBreed (@PathVariable Long id){
        breedService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
