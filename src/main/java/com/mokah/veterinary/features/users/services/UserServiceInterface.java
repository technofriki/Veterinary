package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.features.users.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserServiceInterface {

    List<UserResponse> findAll();

    UserResponse findByExternalId(UUID externalId);

    void delete(UUID externalId);
}
