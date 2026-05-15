package com.mokah.veterinary.features.owners.dto;

import com.mokah.veterinary.features.adresses.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30)
    private String firstName ;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30)
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Size(min = 8, max = 20)
    private String phone;

    @NotBlank(message = "Email is required")
    @Size(max = 100)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "DNI is required")
    @Size(min = 7, max = 15)
    private String dni;

    @Valid
    private AddressRequest address;
}
