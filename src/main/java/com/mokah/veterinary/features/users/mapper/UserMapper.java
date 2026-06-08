package com.mokah.veterinary.features.users.mapper;

import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse (User entity);

    List<UserResponse> toResponse (List<User> entity);
}
