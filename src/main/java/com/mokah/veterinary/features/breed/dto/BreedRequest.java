package com.mokah.veterinary.features.breed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BreedRequest {

    @NotBlank
    @Size(min = 1, max = 50)
private String name;
    @NotBlank
    @Size(min = 1, max = 50)
private String color;

}
