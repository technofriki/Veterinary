package com.mokah.veterinary.features.studies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public record StudyRequest(
        @NotBlank String name,
        @NotBlank String description
) {
}
