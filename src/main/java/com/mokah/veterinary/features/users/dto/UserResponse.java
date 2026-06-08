package com.mokah.veterinary.features.users.dto;

import com.mokah.veterinary.features.users.enums.UserState;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID externalId;
    private String firstName;
    private String lastName;
    private String email;
    private UserState userState;


}
