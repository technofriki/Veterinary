package com.mokah.veterinary.features.users.mapper;

import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse (User entity);

    List<UserResponse> toResponse (List<User> entity);
}
