package com.mokah.veterinary.features.diagnosis.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisResponseDTO {

    private Long id;
    private String descripcion;
    private LocalDateTime fechaDiagnostico;
    private String observaciones;
}
