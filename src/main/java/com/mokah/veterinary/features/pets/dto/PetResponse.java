package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeResponse;
import com.mokah.veterinary.features.breed.dto.BreedResponse;

import java.time.LocalDate;
import java.util.UUID;

public record PetResponse(
        UUID externalId,
        String name,
        LocalDate birthDate,
        boolean active,
        String color,
        AnimalTypeResponse animalType,
        BreedResponse breed,
        Long visitsCount,
        LocalDate lastVisitDate,
        Long activeAppointments
) {}
