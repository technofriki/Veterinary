package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User entityByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User with externalId"+ externalId +"not found"));
    }
    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findByExternalId(UUID externalId){
        User entity = entityByExternalId(externalId);
        return userMapper.toResponse(entity);
    }


    @Override
    public void delete(UUID externalId){
        User entity = entityByExternalId(externalId);
        if(entity.getUserState() == UserState.INACTIVE){
            throw new BusinessRuleException("User with externalId"+ externalId +" has been deleted already");
        }
        entity.setUserState(UserState.INACTIVE);
        userRepository.save(entity);
    }


}
