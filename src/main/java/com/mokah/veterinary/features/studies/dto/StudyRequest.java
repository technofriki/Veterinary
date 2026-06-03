package com.mokah.veterinary.features.studies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
