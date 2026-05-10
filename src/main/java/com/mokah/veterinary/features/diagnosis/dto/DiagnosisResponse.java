package com.mokah.veterinary.features.diagnosis.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResponse {

    private Long id;
    private String description;
    private LocalDateTime dateDiagnosis;
    private String observations;
}
