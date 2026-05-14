package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.features.users.dto.UserRequest;
import com.mokah.veterinary.features.users.dto.UserResponse;

import java.util.List;

public interface UserServiceInterface {

    UserResponse create(UserRequest userRequest);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse update(Long id, UserRequest userRequest);
    void delete (Long id);
    UserResponse findByUserName(String userName);
}
