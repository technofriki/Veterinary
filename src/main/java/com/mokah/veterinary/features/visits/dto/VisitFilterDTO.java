package com.mokah.veterinary.features.visits.dto;

public record VisitFilterDTO(
        Long visitId,
        String veterinarianName,
        String petName
) {
}
