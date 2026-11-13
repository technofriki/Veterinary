package com.mokah.veterinary.features.conditionsbypet.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.conditions.model.Condition;
import com.mokah.veterinary.features.conditions.service.ConditionService;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetDTO;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetResponse;
import com.mokah.veterinary.features.conditionsbypet.dto.ConditionByPetUpdateDTO;
import com.mokah.veterinary.features.conditionsbypet.exception.ConditionByPetExistsException;
import com.mokah.veterinary.features.conditionsbypet.mapper.ConditionByPetMapper;
import com.mokah.veterinary.features.conditionsbypet.model.ConditionByPet;
import com.mokah.veterinary.features.conditionsbypet.repository.ConditionByPetRepository;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.security.enums.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionByPetServiceImpl implements ConditionByPetService {

    private final ConditionByPetRepository repository;
    private final ConditionByPetMapper mapper;
    private final ConditionService conditionService;
    private final PetService petService;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerByPetRepository ownerByPetRepository;

    @Override
    public ConditionByPetResponse create(ConditionByPetDTO dto) {
        Condition condition = conditionService.entityByExternalId(dto.conditionExternalId());

        Pet pet = petService.entityByExternalId(dto.petExternalId());

        if (repository.existsByConditionIdAndPetId(condition.getId(), pet.getId())) {
            throw new ConditionByPetExistsException(
                    "Pet with that condition already exists"
            );
        }

        ConditionByPet entity = mapper.toEntity(dto);

        entity.setCondition(condition);
        entity.setPet(pet);
        entity.setActive(true);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public ConditionByPet entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("ConditionByPet", "externalId", externalId));
    }

    @Override
    public ConditionByPetResponse findById(UUID externalId) {
        ConditionByPet entity = entityByExternalId(externalId);

        // If CLIENT, verify the pet belongs to the current owner
        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        if (clientPetExternalIds != null) {
            UUID petExternalId = entity.getPet().getExternalId();
            if (!clientPetExternalIds.contains(petExternalId)) {
                throw new ResourceNotFoundException("ConditionByPet", "externalId", externalId);
            }
        }

        return mapper.toResponse(entity);
    }

    @Override
    public List<ConditionByPetResponse> findAll() {
        // If CLIENT, filter to only their pets' conditions at the database level
        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        if (clientPetExternalIds != null) {
            return mapper.toResponseList(repository.findByPet_ExternalIdInAndActiveTrue(clientPetExternalIds));
        }
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public List<ConditionByPetResponse> findByPetExternalId(UUID petExternalId) {
        // Verify pet exists
        petService.entityByExternalId(petExternalId);

        // If CLIENT, verify the pet belongs to the current owner
        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        if (clientPetExternalIds != null && !clientPetExternalIds.contains(petExternalId)) {
            throw new ResourceNotFoundException("Pet", "externalId", petExternalId);
        }

        return mapper.toResponseList(repository.findByPet_ExternalIdAndActiveTrue(petExternalId));
    }

    @Override
    public ConditionByPetResponse update(
            UUID externalId,
            ConditionByPetUpdateDTO dto) {

        ConditionByPet entity = entityByExternalId(externalId);

        mapper.update(entity, dto);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void deactivate(UUID externalId) {

        ConditionByPet entity = entityByExternalId(externalId);

        entity.setActive(false);

        repository.save(entity);
    }

    /**
     * Returns the list of pet external IDs owned by the current CLIENT user.
     * Returns null if the current user is not a CLIENT (i.e. has admin-level access).
     * If the user has ROLE_CLIENT along with other roles, it still returns the list
     * to restrict access — but we check if they ALSO have an administrative role.
     */
    private List<UUID> getClientPetExternalIdsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        boolean hasClientRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_CLIENT.name()));

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_ADMIN.name()));

        boolean hasVetRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_VETERINARIAN.name()));

        boolean hasReceptionistRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_RECEPTIONIST.name()));

        // If user has any administrative role alongside CLIENT, grant full access
        if (hasAdminRole || hasVetRole || hasReceptionistRole) {
            return null;
        }

        if (!hasClientRole) {
            return null;
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        Owner owner = ownerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "user", user.getId()));

        List<OwnerByPet> ownerByPets = ownerByPetRepository.findByOwnerId(owner.getId());

        return ownerByPets.stream()
                .map(ownerByPet -> ownerByPet.getPet().getExternalId())
                .collect(Collectors.toList());
    }
}
