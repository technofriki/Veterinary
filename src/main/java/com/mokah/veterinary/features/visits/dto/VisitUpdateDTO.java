package com.mokah.veterinary.features.visits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VisitUpdateDTO(
        @NotBlank String observations,
        @NotNull UUID veterinarianExternalId
) {}
