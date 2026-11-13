package com.mokah.veterinary.features.owners.service;

import com.mokah.veterinary.common.exception.OwnerDniExistsException;
import com.mokah.veterinary.common.exception.ResourceAlreadyExistsException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.owners.dto.OwnerRequest;
import com.mokah.veterinary.features.owners.dto.OwnerResponse;
import com.mokah.veterinary.features.owners.dto.OwnerWithCredentialsRequest;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.mapper.OwnerMapper;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.adresses.model.Address;
import com.mokah.veterinary.features.adresses.repository.AddressRepository;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository repository;
    private final OwnerMapper mapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final AuthService authService;

    @Override
    public OwnerResponse create(OwnerRequest dto) {

        if (repository.existsByDni(dto.dni())) {
            throw new OwnerDniExistsException("Owner with DNI " + dto.dni() + " already exists");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("User with email " + dto.email() + " already exists");
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .userState(UserState.ACTIVE)
                .build();
        user = userRepository.save(user);

        Owner owner = mapper.toEntity(dto);
        owner.setUser(user);

        return mapper.toResponse(repository.save(owner));
    }

    @Override
    @Transactional
    public OwnerResponse createWithCredentials(OwnerWithCredentialsRequest dto) {
        if (repository.existsByDni(dto.dni())) {
            throw new OwnerDniExistsException("Owner with DNI " + dto.dni() + " already exists");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("User with email " + dto.email() + " already exists");
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .userState(UserState.ACTIVE)
                .build();
        user = userRepository.save(user);

        authService.createCredentialsForUser(user, dto.password(), null);

        Address address = addressMapper.toEntity(dto.address());
        address = addressRepository.save(address);

        Owner owner = Owner.builder()
                .phone(dto.phone())
                .dni(dto.dni())
                .address(address)
                .user(user)
                .build();

        return mapper.toResponse(repository.save(owner));
    }

    @Override
    public Owner entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "externalId", externalId));
    }

    @Override
    public OwnerResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<OwnerResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public OwnerResponse findByDni(String dni) {
        Owner owner = repository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "dni", dni));

        return mapper.toResponse(owner);
    }

    @Override
    public OwnerResponse update(UUID externalId, OwnerRequest dto) {

        Owner entity = entityByExternalId(externalId);

        if (!entity.getDni().equals(dto.dni())
                && repository.existsByDni(dto.dni())) {
            throw new ResourceAlreadyExistsException("Owner with DNI already exists");
        }

        if (!entity.getUser().getEmail().equals(dto.email())
                && userRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("User with email already exists");
        }

        mapper.updateEntity(entity, dto);

        if (dto.firstName() != null) {
            entity.getUser().setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            entity.getUser().setLastName(dto.lastName());
        }
        if (dto.email() != null) {
            entity.getUser().setEmail(dto.email());
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}