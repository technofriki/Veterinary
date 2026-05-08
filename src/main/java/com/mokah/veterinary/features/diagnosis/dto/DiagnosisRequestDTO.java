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
    private String description;

    @NotNull
    private LocalDateTime dateDiagnosis;

    @Size(max = 2000)
    private String observations;
}
