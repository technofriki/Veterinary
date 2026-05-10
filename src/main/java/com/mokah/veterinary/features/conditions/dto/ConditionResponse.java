package com.mokah.veterinary.features.conditions.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionResponse {

    private Long id;
    private String name;
    private String description;
}
