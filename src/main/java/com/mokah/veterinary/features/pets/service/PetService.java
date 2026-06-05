package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.model.Pet;

import java.util.List;

public interface PetService {

    PetResponse create (PetRequest request);

    List<PetResponse> findAll();
    Pet entityById(Long id);
    PetResponse findById(Long id);
    PetResponse findByName(String name);

    PetResponse update(Long id, PetRequest request);

    void delete(Long id);

}
