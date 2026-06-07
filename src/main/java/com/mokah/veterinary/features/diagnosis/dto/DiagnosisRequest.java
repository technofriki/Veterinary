package com.mokah.veterinary.features.diagnosis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public record DiagnosisRequest(@NotBlank
                               @Size(max = 1000) String description,

                               @NotNull LocalDateTime dateDiagnosis,

                               @NotBlank String observations,

                               @NotNull Long visitId) {


}
