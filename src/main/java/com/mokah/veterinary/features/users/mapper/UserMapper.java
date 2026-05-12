package com.mokah.veterinary.features.users.mapper;

import com.mokah.veterinary.features.users.dto.UserRequest;
import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity (UserRequest request);

    UserResponse toResponse (UserEntity entity);

}
