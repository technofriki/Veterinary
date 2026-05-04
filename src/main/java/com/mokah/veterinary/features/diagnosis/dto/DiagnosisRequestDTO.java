package com.mokah.veterinary.features.diagnosis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisRequestDTO {

    @NotBlank
    @Size(max = 1000)
    private String descripcion;

    @NotNull
    private LocalDateTime fechaDiagnostico;

    @Size(max = 2000)
    private String observaciones;
}
