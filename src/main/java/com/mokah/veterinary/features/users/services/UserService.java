package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.entity.User;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
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
    public UserResponse findByExternalId(UUID externalId){
        User entity = userRepository.findByExternalId(externalId)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        return userMapper.toResponse(entity);
    }


    @Override
    public void delete(UUID externalId){
        User entity = userRepository.findByExternalId(externalId)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+id));
        userRepository.delete(entity);
    }


}
