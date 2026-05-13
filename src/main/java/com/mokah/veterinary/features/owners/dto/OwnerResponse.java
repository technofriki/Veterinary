package com.mokah.veterinary.features.owners.dto;

import com.mokah.veterinary.features.adresses.dto.AddressResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String dni;
    private AddressResponse address;
}
