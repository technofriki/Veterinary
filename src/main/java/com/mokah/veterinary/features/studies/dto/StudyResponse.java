package com.mokah.veterinary.features.studies.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyResponse {
    private Long id;
    private String name;
    private String description;
}
