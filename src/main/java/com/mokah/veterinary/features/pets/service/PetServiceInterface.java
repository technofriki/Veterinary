package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;

import java.util.List;

public interface PetServiceInterface {

    PetResponse create (PetRequest request);
    List<PetResponse> findAll();
    PetResponse findById(Long id);
    PetResponse findByName(String name);
    PetResponse update(Long id, PetRequest request);
    void delete(Long id);

}
