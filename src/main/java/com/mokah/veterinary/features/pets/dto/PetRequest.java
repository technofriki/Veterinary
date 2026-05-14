package com.mokah.veterinary.features.pets.dto;

import com.mokah.veterinary.features.animaltypes.dto.AnimalTypeRequest;

import com.mokah.veterinary.features.breed.dto.BreedRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank(message = "Birth date must be at least approximate")
    private Date birthDate;

    private AnimalTypeRequest animalType;
    private BreedRequest breed;
}
