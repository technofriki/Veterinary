package com.mokah.veterinary.features.animaltypes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalTypeRequest {

@NotBlank
    @Size(max = 50)
    private String name;
}
