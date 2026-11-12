package com.mokah.veterinary.features.users.services;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.users.dto.UserResponse;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.mapper.UserMapper;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInterface {

    private static final String REVOKED_REFRESH_TOKEN_PREFIX = "REVOKED-";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialsRepository credentialsRepository;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public User entityByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User with externalId " + externalId + " not found"));
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
    @Transactional
    public void delete(UUID externalId){
        User entity = entityByExternalId(externalId);
        if(entity.getUserState() == UserState.INACTIVE){
            throw new BusinessRuleException("User with externalId " + externalId + " has been deleted already");
        }
        entity.setUserState(UserState.INACTIVE);
        userRepository.save(entity);

        credentialsRepository.findByUser(entity).ifPresent(credentials -> {
            credentials.setEnabled(false);
            credentials.setRefreshToken(REVOKED_REFRESH_TOKEN_PREFIX + UUID.randomUUID());
            credentialsRepository.save(credentials);
        });
    }

    @Override
    @Transactional
    public void reactivate(UUID externalId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(auth -> auth.equals("UPDATE_USERS"))) {
            throw new AccessDeniedException("User does not have UPDATE_USERS permission");
        }

        User entity = entityByExternalId(externalId);
        if(entity.getUserState() == UserState.ACTIVE){
            throw new BusinessRuleException("User with externalId " + externalId + " is already active");
        }
        entity.setUserState(UserState.ACTIVE);
        userRepository.save(entity);

        credentialsRepository.findByUser(entity).ifPresent(credentials -> {
            credentials.setEnabled(true);
            credentials.setRefreshToken(REVOKED_REFRESH_TOKEN_PREFIX + UUID.randomUUID());
            credentialsRepository.save(credentials);
        });
    }


}
