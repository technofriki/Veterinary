package com.mokah.veterinary.features.breed.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreedResponse {
    private Long id;
    private String name;
    private String color;
}
