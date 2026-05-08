package com.mokah.veterinary.features.adresses.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdressResponse {

    private Long id;
    private String street;
    private String streetNumber;
    private String city;
    private String province;
    private String country;
}
