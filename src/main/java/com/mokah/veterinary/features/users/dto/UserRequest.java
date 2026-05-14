package com.mokah.veterinary.features.users.dto;

import com.mokah.veterinary.features.users.enums.Role;
import com.mokah.veterinary.features.users.enums.UserState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String username;

    @NotBlank
    @Size(min = 1, max = 50)
    private String password;

    @NotBlank
    @Size(min = 1, max = 50)// Agregar esto
    private UserState userState;

    @NotBlank
    @Size(min = 1, max = 50)
    private Role role;



}
