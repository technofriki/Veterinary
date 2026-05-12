package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.features.users.dto.UserRequest;
import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.entity.UserEntity;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findById(Long id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        return userMapper.toResponse(entity);
    }

    @Override
    public UserResponse findByUserName(String userName) {
        UserEntity entity = userRepository.findAll().stream()
                .filter(u-> u.getUsername().equalsIgnoreCase(userName))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("User not found with name: "+userName));
        return userMapper.toResponse(entity);
    }

    @Override
    public UserResponse create (UserRequest request){
        UserEntity entity = userMapper.toEntity(request);
        UserEntity savedUser = userRepository.save(entity);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public void delete(Long id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        userRepository.delete(entity);
    }

    @Override
    public UserResponse update(Long id, UserRequest request){
        UserEntity existingEntity = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        existingEntity.setUsername(request.getUsername());
        existingEntity.setPassword(request.getPassword());
        existingEntity.setRole(request.getRole());
        existingEntity.setState(request.getState());

        UserEntity updated = userRepository.save(existingEntity);
        return userMapper.toResponse(updated);
    }
}
