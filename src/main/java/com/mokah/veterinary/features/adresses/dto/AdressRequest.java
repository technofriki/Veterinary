package com.mokah.veterinary.features.adresses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdressRequest {

    @NotBlank
    @Size(max = 100)
    private String street;

    @NotBlank
    @Size(max = 50)
    private String streetNumber;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String province;

    @Size(max = 50)
    private String country;
}
