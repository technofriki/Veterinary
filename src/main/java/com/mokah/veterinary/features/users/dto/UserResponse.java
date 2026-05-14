package com.mokah.veterinary.features.users.dto;

import com.mokah.veterinary.features.users.enums.Role;
import com.mokah.veterinary.features.users.enums.UserState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private UserState userState;
    private Role role;

}
