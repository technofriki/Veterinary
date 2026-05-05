package com.mokah.veterinary.features.animaltypes.controller;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;
import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animal-type") ///checkear que sea singular o plural
public class AnimalTypeController {

    private final AnimalTypeService animalTypeService;

    public AnimalTypeController(AnimalTypeService animalTypeService) {
        this.animalTypeService = animalTypeService;
    }

    @GetMapping
    public ResponseEntity<List<AnimalTypeResponse>> getAllAnimalTypes() {
        List<AnimalTypeResponse> responseList = animalTypeService.findAll();
        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnimalTypeResponse> getAnimalTypeById (@PathVariable Long id){
        AnimalTypeResponse response = animalTypeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AnimalTypeResponse> getAnimalTypeByName (@PathVariable String name){
        AnimalTypeResponse response = animalTypeService.findByName(name);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AnimalTypeResponse> createAnimalType (@RequestBody AnimalTypeRequest request){
        AnimalTypeResponse created = animalTypeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<AnimalTypeResponse> updateAnimalType (@PathVariable Long id, @RequestBody AnimalTypeRequest request){
        AnimalTypeResponse updated = animalTypeService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AnimalTypeResponse> deleteAnimalType (@PathVariable Long id){
        animalTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

